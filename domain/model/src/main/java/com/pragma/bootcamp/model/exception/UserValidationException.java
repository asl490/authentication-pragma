package com.pragma.bootcamp.model.exception;

import com.pragma.bootcamp.model.enums.ErrorCode;

public class UserValidationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String resolvedMessage;

    public UserValidationException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.resolvedMessage = null;
    }

    public UserValidationException(ErrorCode errorCode, String resolvedMessage) {
        super(resolvedMessage);
        this.errorCode = errorCode;
        this.resolvedMessage = resolvedMessage;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public String getResolvedMessage() {
        return resolvedMessage != null ? resolvedMessage : errorCode.getCode();
    }
}
