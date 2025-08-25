package com.pragma.bootcamp.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}