package com.pragma.bootcamp.model.user.gateways;

public interface PasswordEncryptionGateway {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
