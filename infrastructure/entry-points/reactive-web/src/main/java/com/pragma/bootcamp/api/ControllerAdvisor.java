package com.pragma.bootcamp.api;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pragma.bootcamp.api.dto.ErrorResponse;
import com.pragma.bootcamp.model.exception.BusinessException;
import com.pragma.bootcamp.model.exception.NotFoundException;
import com.pragma.bootcamp.model.exception.ValidationException;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .code(HttpStatus.NOT_FOUND.name())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .code(HttpStatus.BAD_REQUEST.name())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.CONFLICT.value())
                        .code(HttpStatus.CONFLICT.name())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .code(HttpStatus.BAD_REQUEST.name())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .message("An unexpected error occurred")
                        .timestamp(LocalDateTime.now())
                        .errors(null)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}