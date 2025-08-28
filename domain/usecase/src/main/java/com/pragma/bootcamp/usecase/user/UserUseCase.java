package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.exception.BusinessException;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final TransactionalGateway transactionalGateway;

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
        return transactionalGateway.doInTransaction(userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.<User>error(new BusinessException("Email ya esta registrado")))
                .switchIfEmpty(userRepository.create(user)));
    }

     public Mono<User> findByDocument(String document) {
     return userRepository.findByDocument(document);
     }
}