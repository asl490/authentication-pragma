package com.pragma.bootcamp.model.user;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.enums.Role;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.validation.UserValidation;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTest {

    @Test
    void shouldValidateCorrectEmail() {
        assertDoesNotThrow(() -> UserValidation.validateEmailFormat("user@example.com"));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateEmailFormat(null));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.EMAIL_EMPTY.getCode());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateEmailFormat("invalid@com"));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.INVALID_EMAIL_FORMAT.getCode());
    }

    @Test
    void shouldValidateCorrectSalary() {
        assertDoesNotThrow(() -> UserValidation.validateSalary(BigDecimal.valueOf(10000)));
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateSalary(null));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.SALARY_NULL.getCode());
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNegative() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateSalary(BigDecimal.valueOf(-1)));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.SALARY_NEGATIVE.getCode());
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsTooHigh() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateSalary(BigDecimal.valueOf(20000000)));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.SALARY_TOO_HIGH.getCode());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNullOrEmpty() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateName(null));
        assertThrows(UserValidationException.class, () -> UserValidation.validateName("   "));
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNullOrEmpty() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateLastName(null));
        assertThrows(UserValidationException.class, () -> UserValidation.validateLastName(""));
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsInvalid() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateDocument(null));
        assertThrows(UserValidationException.class, () -> UserValidation.validateDocument("abc"));
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsInvalid() {
        assertThrows(UserValidationException.class, () -> UserValidation.validatePhone(null));
        assertThrows(UserValidationException.class, () -> UserValidation.validatePhone("abc123"));
    }

    @Test
    void shouldThrowExceptionWhenBirthDateIsNull() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateBirthDate(null));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.BIRTHDATE_NULL.getCode());
    }

    @Test
    void shouldThrowExceptionWhenBirthDateIsInFuture() {
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateBirthDate(LocalDate.now().plusDays(1)));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.BIRTHDATE_IN_FUTURE.getCode());
    }

    @Test
    void shouldThrowExceptionWhenUserIsUnderage() {
        LocalDate underageDate = LocalDate.now().minusYears(17);
        UserValidationException ex = assertThrows(UserValidationException.class, () ->
                UserValidation.validateBirthDate(underageDate));
        assertThat(ex.getCode()).isEqualTo(ErrorCode.UNDERAGE.getCode());
    }

    @Test
    void shouldValidateUserSuccessfully() {
        User validUser = User.builder()
                .id("1")
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .document("123456789")
                .phone("3123456789")
                .birthDate(LocalDate.of(1990, 1, 1))
                .salary(BigDecimal.valueOf(5000000))
                .password("secret")
                .role(Role.ADMIN)
                .build();

        assertDoesNotThrow(() -> UserValidation.validate(validUser));
    }

}
