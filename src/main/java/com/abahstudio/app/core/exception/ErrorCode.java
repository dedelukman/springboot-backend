package com.abahstudio.app.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // --- USER ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_EXISTS", "Email already exists"),
    USERNAME_ALREADY_TAKEN(HttpStatus.BAD_REQUEST, "USERNAME_ALREADY_TAKEN", "This username is already taken"),


    // --- FILE / UPLOAD ---
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE_TOO_LARGE", "Uploaded file exceeds allowed size. Photo size must be <= 2MB"),
    FILE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "FILE_INVALID_EXTENSION", "Invalid file extension. Allowed: jpg, jpeg, png"),
    FILE_INVALID_MIME(HttpStatus.BAD_REQUEST, "FILE_INVALID_MIME", "Invalid MIME type"),
    FILE_INVALID_MAGIC_NUMBER(HttpStatus.BAD_REQUEST, "FILE_INVALID_MAGIC_NUMBER", "Invalid file signature / magic number"),

    // --- VALIDATION ---
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request validation error"),
    VERSION_CONFLICT(HttpStatus.CONFLICT,"VERSION_CONFlICT" ,"Version conflict"),

    // --- SERVER ---
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error");



    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }

}
