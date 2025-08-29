package com.pragma.bootcamp.model.user.validation;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public final class UserValidation {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final String DOCUMENT_REGEX = "^[0-9]{7,15}$";
    private static final String PHONE_REGEX = "^[0-9]{9,15}$";

    private UserValidation() {
    }

    public static void validateEmailFormat(String email) {
        if (email == null) {
            throw new UserValidationException(ErrorCode.EMAIL_EMPTY);
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new UserValidationException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    public static void validateSalary(BigDecimal salary) {
        if (salary == null) {
            throw new UserValidationException(ErrorCode.SALARY_NULL);
        }
        if (salary.compareTo(MIN_SALARY) < 0) {
            throw new UserValidationException(ErrorCode.SALARY_NEGATIVE);
        }
        if (salary.compareTo(MAX_SALARY) > 0) {
            throw new UserValidationException(ErrorCode.SALARY_TOO_HIGH);
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserValidationException(ErrorCode.NAME_EMPTY);
        }
    }

    public static void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new UserValidationException(ErrorCode.LASTNAME_EMPTY);
        }
    }

    public static void validateDocument(String document) {
        if (document == null || !Pattern.matches(DOCUMENT_REGEX, document)) {
            throw new UserValidationException(ErrorCode.INVALID_DOCUMENT);
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || !Pattern.matches(PHONE_REGEX, phone)) {
            throw new UserValidationException(ErrorCode.INVALID_PHONE);
        }
    }

    public static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new UserValidationException(ErrorCode.BIRTHDATE_NULL);
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new UserValidationException(ErrorCode.BIRTHDATE_IN_FUTURE);
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new UserValidationException(ErrorCode.UNDERAGE);
        }
    }

    public static void validate(User user) {
        validateName(user.getName());
        validateLastName(user.getLastName());
        validateEmailFormat(user.getEmail());
        validateDocument(user.getDocument());
        validatePhone(user.getPhone());
        validateBirthDate(user.getBirthDate());
        validateSalary(user.getSalary());
    }
}