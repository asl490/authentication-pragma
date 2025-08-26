package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Flux<User> getAll();
    Mono<User> update(User userUpdate);
    Mono<Void> delete(Long idUser);
    Mono<User> create(User user);
}
