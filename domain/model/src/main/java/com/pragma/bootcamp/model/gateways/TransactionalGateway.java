package com.pragma.bootcamp.model.gateways;

import reactor.core.publisher.Mono;

public interface TransactionalGateway {
    <T> Mono<T> doInTransaction(Mono<T> operations);
}
