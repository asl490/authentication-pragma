package com.pragma.bootcamp.model.user;

import com.pragma.bootcamp.model.enums.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
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
    private String password;
    private Role role;

}