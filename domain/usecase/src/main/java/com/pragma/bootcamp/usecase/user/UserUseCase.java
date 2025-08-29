package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.model.user.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Flux<User> getAll() {
        return userRepository.getAll();
    }

    public Mono<User> findById(UUID userId) {
        if (userId == null) {
            return Mono.error(new UserValidationException(ErrorCode.ID_NULL));
        }
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)));
    }

    public Mono<User> findByDocument(String document) {
        if (document == null || document.trim().isEmpty()) {
            return Mono.error(new UserValidationException(ErrorCode.INVALID_DOCUMENT));
        }
        return userRepository.findByDocument(document);
    }

    public Mono<User> create(User user) {
        return validateUser(user)
                .flatMap(this::validateEmailNotExists)
                .flatMap(this::validateDocumentNotExists)
                .flatMap(userRepository::create);
    }
    public Mono<User> update(User updateUser) {
        return
                validateUser(updateUser)
                        .flatMap(user -> userRepository.findById(user.getId())
                                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)))
                                .flatMap(existing -> userRepository.update(user))
                        );
    }

    public Mono<Void> delete(UUID userId) {
        if (userId == null) {
            return Mono.error(new UserValidationException(ErrorCode.ID_NULL));
        }
        return
                userRepository.findById(userId)
                        .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.USER_NOT_FOUND)))
                        .flatMap(user -> userRepository.delete(userId)
                        );
    }

    private Mono<User> validateUser(User user) {
        try {
            UserValidation.validate(user);
            return Mono.just(user);
        } catch (UserValidationException e) {
            return Mono.error(e);
        }
    }

    private Mono<User> validateEmailNotExists(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.<User>error(new UserValidationException(ErrorCode.EMAIL_DUPLICATED)))
                .switchIfEmpty(Mono.just(user));
    }

    private Mono<User> validateDocumentNotExists(User user) {
        return userRepository.findByDocument(user.getDocument())
                .flatMap(existing -> Mono.<User>error(new UserValidationException(ErrorCode.DOCUMENT_DUPLICATED)))
                .switchIfEmpty(Mono.just(user));
    }

}
