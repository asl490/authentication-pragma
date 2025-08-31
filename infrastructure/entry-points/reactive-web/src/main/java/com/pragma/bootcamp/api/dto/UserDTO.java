package com.pragma.bootcamp.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private String id;
    private String name;
    private String lastName;
    private String document;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private BigDecimal salary;

}
