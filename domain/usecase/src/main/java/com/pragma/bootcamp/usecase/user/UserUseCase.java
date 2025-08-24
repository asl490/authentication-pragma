package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Flux<User> getAll() {
        return userRepository.getAll();
    }

    public Mono<User> update(User userUpdate) {
        return userRepository.update(userUpdate);
    }

    public Mono<Void> delete(Long idUser) {
        return userRepository.delete(idUser);
    }

    public Mono<User> create(User user) {
        return userRepository.create(user);
    }

    // public Flux<User> findByTitle(String title) {
    // return userRepository.findByTitle(title);
    // }
}
