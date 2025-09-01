package com.pragma.bootcamp.model.user;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationExceptionTest {

    @Test
    void shouldReturnCodeAndFallbackMessageWhenNoResolvedMessage() {
        UserValidationException exception =
                new UserValidationException(ErrorCode.INVALID_EMAIL_FORMAT);

        assertThat(exception.getCode()).isEqualTo(ErrorCode.INVALID_EMAIL_FORMAT.getCode());

    }

    @Test
    void shouldReturnResolvedMessageWhenProvided() {
        String resolvedMsg = "El correo electrónico no cumple el formato requerido";
        UserValidationException exception =
                new UserValidationException(ErrorCode.INVALID_EMAIL_FORMAT, resolvedMsg);

        assertThat(exception.getCode()).isEqualTo("EMAIL_INVALID");
        assertThat(exception.getResolvedMessage()).isEqualTo(resolvedMsg); // custom message
        assertThat(exception.getMessage()).isEqualTo(resolvedMsg); // from super()
    }
}

