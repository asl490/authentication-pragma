package com.pragma.bootcamp.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.pragma.bootcamp.model.exception.BusinessException;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@Primary
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, UserReactiveRepository>
        implements UserRepository {
    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Flux<User> getAll() {

        return super.findAll();
    }

    @Override
    public Mono<User> update(User userUserUpdate) {

        return super.save(userUserUpdate);
    }

    @Override
    public Mono<Void> delete(Long idUser) {

        return repository.deleteById(idUser);
    }

    @Override
    public Mono<User> create(User user) {
        log.trace("Saving new user with email: {}", user.getEmail());
        log.error("errror: {}", user.getEmail());
        return super.save(user)
                .doOnSuccess(savedUser -> log.info("Successfully created user with ID: {}", savedUser.getId()))
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    String message = ex.getMessage().toLowerCase();
                    if (message.contains("users.document") || message.contains("users_document_key")) {
                        log.warn("Attempt to create a user with an already registered document: {}",
                                user.getDocument());
                        return new BusinessException("The document is already registered.");
                    }
                    log.error("Unexpected DataIntegrityViolationException", ex);
                    return ex;
                });
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .switchIfEmpty(Mono.empty())
                .map(entity -> mapper.map(entity, User.class));
    }

}
