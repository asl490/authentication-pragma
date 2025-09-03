package com.pragma.bootcamp.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
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
    private Long roleId;

}