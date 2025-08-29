package com.pragma.bootcamp.r2dbc;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.DataIntegrityViolationException;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository>
        implements UserRepository {

    private final TransactionalGateway transactionalGateway;

    public UserRepositoryAdapter(UserReactiveRepository repository,
                                 ObjectMapper mapper,
                                 TransactionalGateway transactionalGateway) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.transactionalGateway = transactionalGateway;
    }

    @Override
    public Flux<User> getAll() {
        return super.findAll();
    }

    @Override
    public Mono<User> update(User userToUpdate) {
        if (userToUpdate.getId() == null) {
            return Mono.error(new UserValidationException(ErrorCode.ID_NULL));
        }
        log.trace("Updating user with ID: {}", userToUpdate.getId());
        return repository.findById(userToUpdate.getId())
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)))
                .flatMap(existingEntity -> {
                    UserEntity updatedEntity = mapper.map(userToUpdate, UserEntity.class);
                    return transactionalGateway.doInTransaction(
                            repository.save(updatedEntity)
                                    .doOnSuccess(e -> log.info("Updated user with ID: {}", e.getId()))
                    );
                })
                .map(entity -> mapper.map(entity, User.class))
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    log.error("Unexpected error during update", ex);
                    return new DataIntegrityViolationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
                });
    }

    @Override
    public Mono<User> create(User user) {
        log.trace("Persisting new user with email: {}", user.getEmail());

        return transactionalGateway.doInTransaction(
                super.save(user)
                        .doOnSuccess(savedUser ->
                                log.info("Successfully created user with ID: {}", savedUser.getId())
                        ).onErrorMap(DataIntegrityViolationException.class, ex -> {
                            log.error("Unexpected DataIntegrityViolationException during create()", ex);
                            return new DataIntegrityViolationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
                        })

        );
    }

    @Override
    public Mono<Void> delete(UUID userId) {
        return transactionalGateway.doInTransaction(repository.deleteById(userId));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> findByDocument(String document) {
        return repository.findByDocument(document)
                .map(entity -> mapper.map(entity, User.class));
    }
}
