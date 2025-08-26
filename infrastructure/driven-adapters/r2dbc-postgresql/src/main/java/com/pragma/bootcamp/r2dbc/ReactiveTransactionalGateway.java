package com.pragma.bootcamp.r2dbc;

import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveTransactionalGateway implements TransactionalGateway {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> doInTransaction(Mono<T> operations) {
        return operations.as(transactionalOperator::transactional);
    }
}
