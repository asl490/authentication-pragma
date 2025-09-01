package com.pragma.bootcamp.r2dbc;

import com.pragma.bootcamp.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<RoleEntity, Integer> {
    Mono<RoleEntity> findByName(String name);
}
