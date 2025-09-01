package com.pragma.bootcamp.r2dbc;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.enums.Role;
import com.pragma.bootcamp.model.exception.DataIntegrityViolationException;
import com.pragma.bootcamp.model.exception.NotFoundException;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.r2dbc.entity.RoleEntity;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;
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

    public UserRepositoryAdapter(UserReactiveRepository repository,
                                 ObjectMapper mapper, RoleRepository roleRepository,
                                 TransactionalGateway transactionalGateway) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.roleRepository = roleRepository;

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

        Mono<UserEntity> userEntityMono = Mono.just(userToUpdate)
                .flatMap(user -> roleRepository.findByName(user.getRole().name())
                        .switchIfEmpty(Mono.error(new NotFoundException(ErrorCode.ROLE_NOT_FOUND.toString())))
                        .map(roleEntity -> mapToEntity(user, roleEntity.getId()))
                );

        return super.findById(userToUpdate.getId())
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)))
                .flatMap(existingEntity -> userEntityMono)
                .flatMap(repository::save)
                .flatMap(this::mapToUserWithRole)
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    log.error("Unexpected error during update", ex);
                    return new DataIntegrityViolationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
                });
    }

    @Override
    public Mono<User> create(User user) {
        log.trace("Persisting new user with email: {}", user.getEmail());

        Mono<UserEntity> userEntityMono = Mono.just(user)
                .flatMap(u -> roleRepository.findByName(u.getRole().name())
                        .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.ROLE_NOT_FOUND)))
                        .map(roleEntity -> mapToEntity(u, roleEntity.getId()))
                );

        return transactionalGateway.doInTransaction(
                userEntityMono.flatMap(repository::save)
                        .flatMap(this::mapToUserWithRole)
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
                .flatMap(this::mapToUserWithRole);
    }

    @Override
    public Mono<User> findByDocument(String document) {
        return repository.findByDocument(document)
                .flatMap(this::mapToUserWithRole);
    }

    private Mono<User> mapToUserWithRole(UserEntity userEntity) {
        return roleRepository.findById(userEntity.getRoleId())
                .switchIfEmpty(Mono.error(new NotFoundException(ErrorCode.ROLE_NOT_FOUND.toString())))
                .map(roleEntity -> mapToUser(userEntity, roleEntity));
    }

    private User mapToUser(UserEntity userEntity, RoleEntity roleEntity) {
        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .document(userEntity.getDocument())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .address(userEntity.getAddress())
                .birthDate(userEntity.getBirthDate())
                .salary(userEntity.getSalary())
                .password(userEntity.getPassword())
                .role(Role.valueOf(roleEntity.getName()))
                .build();
    }

    private UserEntity mapToEntity(User user, Integer roleId) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .document(user.getDocument())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .salary(user.getSalary())
                .password(user.getPassword())
                .roleId(roleId)
                .build();
    }
}
