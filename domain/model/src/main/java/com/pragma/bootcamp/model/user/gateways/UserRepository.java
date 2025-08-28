package com.pragma.bootcamp.model.user.gateways;

import com.pragma.bootcamp.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Flux<User> getAll();

    Mono<User> update(User userUserUpdate);

    Mono<Void> delete(Long idUser);

    Mono<User> create(User userUser);

    Mono<User> findByEmail(String email);

    Mono<User> findByDocument(String document);
}
