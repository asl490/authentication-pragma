package com.pragma.bootcamp.model.user;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.validation.UserValidation;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserValidationTest {

    @Test
    void validateSalary_validSalary_shouldPass() {
        StepVerifier.create(UserValidation.validateSalary(BigDecimal.valueOf(1000000)))
                .verifyComplete();
    }

    @Test
    void validateSalary_negativeSalary_shouldFail() {
        StepVerifier.create(UserValidation.validateSalary(BigDecimal.valueOf(-1)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.SALARY_NEGATIVE.getCode());
                })
                .verify();
    }

    @Test
    void validateSalary_tooHighSalary_shouldFail() {
        StepVerifier.create(UserValidation.validateSalary(new BigDecimal("20000000")))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.SALARY_TOO_HIGH.getCode());
                })
                .verify();
    }

    @Test
    void validateDocument_valid_shouldPass() {
        StepVerifier.create(UserValidation.validateDocument("12345678"))
                .verifyComplete();
    }

    @Test
    void validateDocument_invalid_shouldFail() {
        StepVerifier.create(UserValidation.validateDocument("abc123"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.INVALID_DOCUMENT.getCode());
                })
                .verify();
    }

    @Test
    void validatePhone_valid_shouldPass() {
        StepVerifier.create(UserValidation.validatePhone("123456789"))
                .verifyComplete();
    }

    @Test
    void validatePhone_invalid_shouldFail() {
        StepVerifier.create(UserValidation.validatePhone("phone123"))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.INVALID_PHONE.getCode());
                })
                .verify();
    }

    @Test
    void validateBirthDate_valid_shouldPass() {
        StepVerifier.create(UserValidation.validateBirthDate(LocalDate.now().minusYears(20)))
                .verifyComplete();
    }

    @Test
    void validateBirthDate_inFuture_shouldFail() {
        StepVerifier.create(UserValidation.validateBirthDate(LocalDate.now().plusDays(1)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.BIRTHDATE_IN_FUTURE.getCode());
                })
                .verify();
    }

    @Test
    void validateBirthDate_underage_shouldFail() {
        StepVerifier.create(UserValidation.validateBirthDate(LocalDate.now().minusYears(17)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserValidationException;
                    assert Objects.equals(((UserValidationException) error).getResolvedMessage(), ErrorCode.UNDERAGE.getCode());
                })
                .verify();
    }

    @Test
    void validateUser_valid_shouldPass() {
        User user = new User();
        user.setDocument("12345678");
        user.setPhone("123456789");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setSalary(BigDecimal.valueOf(1000000));

        StepVerifier.create(UserValidation.validate(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void validateUser_invalidDocument_shouldFail() {
        User user = new User();
        user.setDocument("invalid");
        user.setPhone("123456789");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setSalary(BigDecimal.valueOf(1000000));

        StepVerifier.create(UserValidation.validate(user))
                .expectError(UserValidationException.class)
                .verify();
    }

    @Test
    void shouldReturnResolvedMessageWhenProvided() {
        UserValidationException exception = new UserValidationException(ErrorCode.INVALID_DOCUMENT);

        // Verifica que el código esté correctamente seteado
        assertEquals("USR_009", exception.getCode());

        // Verifica que el mensaje generado sea el esperado
        assertEquals("INVALID_DOCUMENT", ErrorCode.INVALID_DOCUMENT.toString());
    }
}
