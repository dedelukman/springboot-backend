package com.abahstudio.app.core.exception;

public class FileValidationException extends ApiException {

    public FileValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileValidationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}