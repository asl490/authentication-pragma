package com.pragma.bootcamp.model.exception;

import com.pragma.bootcamp.model.enums.ErrorCode;

public class UserValidationException extends RuntimeException {

    public UserValidationException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}