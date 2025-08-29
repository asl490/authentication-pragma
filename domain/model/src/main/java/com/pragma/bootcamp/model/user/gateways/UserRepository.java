package com.pragma.bootcamp.model.user.gateways;

import com.pragma.bootcamp.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
    Flux<User> getAll();

    Mono<User> create(User user);

    Mono<User> update(User user);

    Mono<Void> delete(UUID userId);

    Mono<User> findById(UUID userId);

    Mono<User> findByEmail(String email);

    Mono<User> findByDocument(String document);
}