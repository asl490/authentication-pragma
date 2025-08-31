package com.pragma.bootcamp.model.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

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