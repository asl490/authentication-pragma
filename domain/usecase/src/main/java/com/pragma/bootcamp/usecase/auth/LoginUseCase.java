package com.pragma.bootcamp.usecase.auth;

import com.pragma.bootcamp.model.auth.gateways.TokenGateway;
import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.LoginAttemptGateway;
import com.pragma.bootcamp.model.gateways.PasswordEncryptionGateway;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptionGateway passwordGateway;
    private final TokenGateway tokenGateway;
    private final LoginAttemptGateway loginAttemptGateway;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.AUTHENTICATION_FAILED)))
                .flatMap(user ->
                        passwordGateway.matches(password, user.getPassword())
                                .flatMap(matches -> {
                                    if (Boolean.TRUE.equals(matches)) {
                                        return tokenGateway.generateToken(user);
                                    } else {
                                        return Mono.error(new UserValidationException(ErrorCode.AUTHENTICATION_FAILED));
                                    }
                                })
                );
    }

    public Mono<String> loginWithBlock(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserValidationException(ErrorCode.AUTHENTICATION_FAILED)))
                .flatMap(user ->
                        // Primero validar si el usuario está bloqueado
                        loginAttemptGateway.isUserBlocked(email)
                                .flatMap(isBlocked -> {
                                    if (isBlocked) {
                                        return Mono.error(new UserValidationException(ErrorCode.USER_IS_BLOCKED));
                                    }
                                    // Proceder con autenticación si no está bloqueado
                                    return passwordGateway.matches(password, user.getPassword())
                                            .flatMap(matches -> {
                                                if (Boolean.TRUE.equals(matches)) {
                                                    // Login exitoso - limpiar intentos y generar token
                                                    return loginAttemptGateway.clearAttempts(email)
                                                            .then(tokenGateway.generateToken(user));
                                                } else {
                                                    // Login fallido - registrar intento y lanzar error
                                                    return loginAttemptGateway.recordFailedAttempt(email)
                                                            .then(Mono.error(new UserValidationException(
                                                                    ErrorCode.AUTHENTICATION_FAILED)));
                                                }
                                            });
                                }));
    }

}