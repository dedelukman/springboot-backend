package com.abahstudio.app.core.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------- Custom Business Exceptions -----------
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest request) {
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileValidation(FileValidationException ex,
                                                              HttpServletRequest request) {
        log.warn("File validation failed: {}", ex.getMessage());
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage(), request);
    }

    // ----------- Spring Framework Exceptions -----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        // Collect ALL validation errors
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", detail);
        return buildErrorResponse(ErrorCode.BAD_REQUEST, detail, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String detail = String.format("Parameter '%s' should be of type %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("Type mismatch: {}", detail);
        return buildErrorResponse(ErrorCode.BAD_REQUEST, detail, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex,
                                                        HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.PAGE_NOT_FOUND, "Resource not found", request);
    }

    // ----------- Database / Concurrency Exceptions -----------
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException ex,
                                                              HttpServletRequest request) {

        log.warn("Optimistic lock conflict: {}", ex.getMessage());

        return buildErrorResponse(
                ErrorCode.VERSION_CONFLICT,
                "The data has been updated by another user. Please refresh your page.",
                request
        );
    }

    // ----------- Security Critical: Generic Exception -----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        // Log full error with stack trace for debugging
        log.error("Unhandled exception: ", ex);

        // But return generic message to client
        String clientMessage = "An unexpected error occurred. Please contact support.";

        return buildErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                clientMessage,  // Never expose exception message!
                request
        );
    }

    // ----------- Helper Method -----------
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode,
                                                             String detail,
                                                             HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse( errorCode,  detail,  request.getRequestURI());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}