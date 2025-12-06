package com.abahstudio.app.core.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String code;
    private String message;
    private String detail;
    private String path;

    public ErrorResponse(ErrorCode errorCode, String detail, String path) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.detail = detail;
        this.path = path;
    }

}
