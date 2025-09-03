package com.pragma.bootcamp.model.user.gateways;

import com.pragma.bootcamp.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Flux<User> getAll();

    Mono<User> create(User user);

    Mono<User> update(User user);

    Mono<Void> delete(String userId);

    Mono<User> findById(String userId);

    Mono<User> findByEmail(String email);

    Mono<User> findByDocument(String document);
}