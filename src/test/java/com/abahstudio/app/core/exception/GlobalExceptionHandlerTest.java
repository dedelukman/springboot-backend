package com.abahstudio.app.core.exception;


import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleApiException_shouldReturnProperResponse() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/users");

        ApiException ex = new ApiException(ErrorCode.USER_NOT_FOUND, "User with id 123 not found");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleApi(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
        assertThat(response.getBody().getDetail()).isEqualTo("User with id 123 not found");
        assertThat(response.getBody().getPath()).isEqualTo("/api/users");
    }

    @Test
    void handleValidationException_shouldReturnAllErrors() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/users");

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("user", "email", "Email must be valid");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().getDetail()).contains("email: Email must be valid");
    }

    @Test
    void handleGeneralException_shouldNotExposeInternalDetails() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/users");

        Exception ex = new RuntimeException("Database connection failed: password=secret123");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneral(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail())
                .isEqualTo("An unexpected error occurred. Please contact support."); // Generic message
        assertThat(response.getBody().getDetail())
                .doesNotContain("Database", "password", "secret123"); // No sensitive info
    }

    @Test
    void handleOptimisticLock_shouldReturnVersionConflictError() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        OptimisticLockException ex = new OptimisticLockException("Row was updated");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        ResponseEntity<ErrorResponse> response =
                handler.handleOptimisticLock(ex, request);

        // Assert
        assertThat(response.getStatusCode())
                .isEqualTo(ErrorCode.VERSION_CONFLICT.getStatus());

        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getDetail())
                .isEqualTo("The data has been updated by another user. Please refresh your page.");
        assertThat(body.getPath()).isEqualTo("/api/test");
    }
}