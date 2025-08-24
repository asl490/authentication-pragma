package com.pragma.bootcamp.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pragma.bootcamp.r2dbc.entity.UserEntity;

public interface UserReactiveRepository
                extends ReactiveCrudRepository<UserEntity, Long>, ReactiveQueryByExampleExecutor<UserEntity> {

}
