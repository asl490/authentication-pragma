package com.pragma.bootcamp.model.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncryptionGateway {
    Mono<String> encode(String rawPassword);

    Mono<Boolean> matches(String rawPassword, String encodedPassword);
}
