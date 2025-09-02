package com.pragma.bootcamp.model.exception;

import com.pragma.bootcamp.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthenticationException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

}
