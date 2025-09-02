package com.pragma.bootcamp.model.user.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.User;

import reactor.core.publisher.Mono;

public final class UserValidation {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final String DOCUMENT_REGEX = "^[0-9]{7,15}$";
    private static final String PHONE_REGEX = "^[0-9]{9,15}$";

    public static Mono<Void> validateEmailFormat(String email) {
        if (email == null) {
            return Mono.error(new UserValidationException(ErrorCode.EMAIL_EMPTY));
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_EMAIL_FORMAT));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateSalary(BigDecimal salary) {
        if (salary == null) {
            return Mono.error(new UserValidationException(ErrorCode.SALARY_NULL));
        }
        if (salary.compareTo(MIN_SALARY) < 0) {
            return Mono.error(new UserValidationException(ErrorCode.SALARY_NEGATIVE));
        }
        if (salary.compareTo(MAX_SALARY) > 0) {
            return Mono.error(new UserValidationException(ErrorCode.SALARY_TOO_HIGH));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new UserValidationException(ErrorCode.NAME_EMPTY));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return Mono.error(new UserValidationException(ErrorCode.LASTNAME_EMPTY));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateDocument(String document) {
        if (document == null || !Pattern.matches(DOCUMENT_REGEX, document)) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_DOCUMENT));
        }
        return Mono.empty();
    }

    public static Mono<Void> validatePhone(String phone) {
        if (phone == null || !Pattern.matches(PHONE_REGEX, phone)) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_PHONE));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return Mono.error(new UserValidationException(ErrorCode.BIRTHDATE_NULL));
        }
        if (birthDate.isAfter(LocalDate.now())) {
            return Mono.error(new UserValidationException(ErrorCode.BIRTHDATE_IN_FUTURE));
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            return Mono.error(new UserValidationException(ErrorCode.UNDERAGE));
        }
        return Mono.empty();
    }

    public static Mono<User> validate(User user) {
        return Mono.when(
                validateName(user.getName()),
                validateLastName(user.getLastName()),
                validateEmailFormat(user.getEmail()),
                validateDocument(user.getDocument()),
                validatePhone(user.getPhone()),
                validateBirthDate(user.getBirthDate()),
                validateSalary(user.getSalary()))
                .thenReturn(user);
    }

    private UserValidation() {
    }
}
