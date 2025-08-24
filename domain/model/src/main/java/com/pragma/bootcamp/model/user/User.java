package com.pragma.bootcamp.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pragma.bootcamp.model.user.validation.UserValidation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;
    private String name;
    private String lastName;
    private String document;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private BigDecimal salary;

    public User(Long id, String name, String lastName, String document, String phone, String email, String address,
            LocalDate birthDate, BigDecimal salary) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede ser nulo o vacío");
        }
        if (salary == null) {
            throw new IllegalArgumentException("El salario no puede ser nulo");
        }

        UserValidation.validateEmailFormat(email);
        UserValidation.validateSalary(salary);

        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.salary = salary;
    }
}