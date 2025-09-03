package com.pragma.bootcamp.model.auth.gateways;

import com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Mono;

public interface TokenGateway {
    Mono<String> generateToken(User user);
}