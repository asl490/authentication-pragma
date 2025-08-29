package com.pragma.bootcamp.model.exception;

public class TechnicalException extends RuntimeException {
    public TechnicalException(String message, DataIntegrityViolationException ex) {
        super(message);
    }
}
