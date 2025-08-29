package com.pragma.bootcamp.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pragma.bootcamp.r2dbc.entity.UserEntity;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserReactiveRepository
        extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<UserEntity> findByEmail(String email);
    Mono<UserEntity> findByDocument(String document);
}
