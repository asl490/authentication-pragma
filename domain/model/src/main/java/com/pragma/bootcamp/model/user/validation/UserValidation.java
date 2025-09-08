package com.pragma.bootcamp.model.user.validation;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public final class UserValidation {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final String DOCUMENT_REGEX = "^[0-9]{7,15}$";
    private static final String PHONE_REGEX = "^[0-9]{9,15}$";

    private UserValidation() {
    }

    public static Mono<Void> validateSalary(BigDecimal salary) {
        if (salary.compareTo(MIN_SALARY) < 0) {
            return Mono.error(new UserValidationException(ErrorCode.SALARY_NEGATIVE));
        }
        if (salary.compareTo(MAX_SALARY) > 0) {
            return Mono.error(new UserValidationException(ErrorCode.SALARY_TOO_HIGH));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateDocument(String document) {
        if (!Pattern.matches(DOCUMENT_REGEX, document)) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_DOCUMENT));
        }
        return Mono.empty();
    }

    public static Mono<Void> validatePhone(String phone) {
        if (!Pattern.matches(PHONE_REGEX, phone)) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_PHONE));
        }
        return Mono.empty();
    }

    public static Mono<Void> validateBirthDate(LocalDate birthDate) {

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

                        validateDocument(user.getDocument()),
                        validatePhone(user.getPhone()),
                        validateBirthDate(user.getBirthDate()),
                        validateSalary(user.getSalary()))
                .thenReturn(user);
    }

}
