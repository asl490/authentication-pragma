package com.pragma.bootcamp.usecase.auth;

import com.pragma.bootcamp.model.gateways.LoginAttemptGateway;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidateLoginAttemptUseCase {

    private final LoginAttemptGateway loginAttemptGateway;

    public Mono<Boolean> validateUser(String username) {
        return loginAttemptGateway.isUserBlocked(username)
                .map(blocked -> !blocked);
    }

    public Mono<Void> handleFailedLogin(String username) {
        return loginAttemptGateway.recordFailedAttempt(username);
    }

    public Mono<Void> handleSuccessfulLogin(String username) {
        return loginAttemptGateway.clearAttempts(username);
    }
}