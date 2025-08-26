package com.pragma.bootcamp.infrastructure.usecaseimpl;

import com.pragma.bootcamp.model.exception.BusinessException;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;
    private final TransactionalGateway transactionalGateway;

    @Override
    public Flux<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public Mono<User> update(User userUpdate) {
        return transactionalGateway.doInTransaction(
                userRepository.update(userUpdate)
        );
    }

    @Override
    public Mono<Void> delete(Long idUser) {
        return transactionalGateway.doInTransaction(
                userRepository.delete(idUser).then()
        ).then();
    }

    @Override
    public Mono<User> create(User user) {
        return transactionalGateway.doInTransaction(
            userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.<User>error(new BusinessException("Email ya esta registrado")))
                .switchIfEmpty(userRepository.create(user))
        );
    }
}
