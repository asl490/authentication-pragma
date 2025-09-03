package com.pragma.bootcamp.model.exception;

import com.pragma.bootcamp.model.enums.ErrorCode;

public class DataIntegrityViolationException extends RuntimeException {
    public DataIntegrityViolationException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}