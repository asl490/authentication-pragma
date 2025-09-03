package com.pragma.bootcamp.model.gateways;

import reactor.core.publisher.Mono;

public interface LoginAttemptGateway {
    Mono<Boolean> isUserBlocked(String username);

    Mono<Void> recordFailedAttempt(String username);

    Mono<Void> clearAttempts(String username);

    Mono<Integer> getAttemptCount(String username);
}