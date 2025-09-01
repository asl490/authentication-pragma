package com.pragma.bootcamp.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {

    @NotBlank(message = "El nombre no puede ser nulo o vacío")
    private String name;

    @NotBlank(message = "El apellido no puede ser nulo o vacío")
    private String lastName;
    @NotBlank(message = "El documento no puede ser nulo o vacío")
    private String document;
    private String phone;

    @NotBlank(message = "El correo electrónico no puede ser nulo o vacío")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    private String address;
    private LocalDate birthDate;

    @NotNull(message = "El salario no puede ser nulo")
    @Min(value = 0, message = "El salario no puede ser negativo")
    @Max(value = 15000000, message = "El salario no puede ser mayor a 15,000,000")
    private BigDecimal salary;

    @NotBlank(message = "La contraseña no puede ser nula o vacía")
    private String password;

    @NotBlank(message = "El rol no puede ser nulo o vacío")
    private String role;

}