package com.pragma.bootcamp.r2dbc;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.DataIntegrityViolationException;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import com.pragma.bootcamp.r2dbc.mapper.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository>
        implements UserRepository {

    private final RoleRepository roleRepository;
    private final TransactionalGateway transactionalGateway;
    private final UserEntityMapper userEntityMapper;

    public UserRepositoryAdapter(UserReactiveRepository repository,
                                 ObjectMapper mapper, RoleRepository roleRepository,
                                 TransactionalGateway transactionalGateway, UserEntityMapper userEntityMapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.roleRepository = roleRepository;

        this.transactionalGateway = transactionalGateway;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public Flux<User> getAll() {
        return repository.findAll()
                .map(userEntityMapper::toUser);
    }

    @Override
    public Mono<User> update(User userToUpdate) {
        if (userToUpdate.getId() == null) {
            return Mono.error(new UserValidationException(ErrorCode.ID_NULL));
        }
        log.trace("Updating user with ID: {}", userToUpdate.getId());

        return super.findById(userToUpdate.getId())
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)))
                .then(Mono.just(userEntityMapper.toEntity(userToUpdate))) // ¡Conversión directa!
                .flatMap(repository::save)
                .map(userEntityMapper::toUser) // ¡Una sola operación!
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    log.error("Unexpected error during update", ex);
                    return new DataIntegrityViolationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
                });
    }

    @Override
    public Mono<User> create(User user) {
        log.trace("Persisting new user with email: {}", user.getEmail());

        return transactionalGateway.doInTransaction(
                Mono.just(userEntityMapper.toEntity(user)) // ¡Conversión directa!
                        .flatMap(repository::save)
                        .map(userEntityMapper::toUser)
                        .doOnSuccess(savedUser -> log.info("Successfully created user with ID: {}", savedUser.getId()))
                        .onErrorMap(RuntimeException.class, ex -> {
                            log.error("Unexpected DataIntegrityViolationException during create()", ex);
                            return new RuntimeException(ex.getMessage());
                        })
        );
    }

    @Override
    public Mono<Void> delete(String userId) {
        return transactionalGateway.doInTransaction(repository.deleteById(userId));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userEntityMapper::toUser);
    }

    @Override
    public Mono<User> findByDocument(String document) {
        return repository.findByDocument(document)
                .map(userEntityMapper::toUser);
    }
}