package com.pragma.bootcamp.usecase.auth;

import com.pragma.bootcamp.model.auth.gateways.TokenGateway;
import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.exception.AuthenticationException;
import com.pragma.bootcamp.model.gateways.PasswordEncryptionGateway;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptionGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationException(ErrorCode.AUTHENTICATION_FAILED)))
                .flatMap(user ->
                        passwordGateway.matches(password, user.getPassword())
                                .flatMap(matches -> {
                                    if (Boolean.TRUE.equals(matches)) {
                                        return tokenGateway.generateToken(user);
                                    } else {
                                        return Mono.error(new AuthenticationException(ErrorCode.AUTHENTICATION_FAILED));
                                    }
                                })
                );
    }
}
