package com.pragma.bootcamp.model.user.validation;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class UserValidation {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    public static void validateEmailFormat(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }
    }

    public static void validateSalary(BigDecimal salary) {
        if (salary.compareTo(MIN_SALARY) < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo");
        }
        if (salary.compareTo(MAX_SALARY) > 0) {
            throw new IllegalArgumentException("El salario no puede ser mayor a 15,000,000");
        }
    }

    private UserValidation() {
    }
}
