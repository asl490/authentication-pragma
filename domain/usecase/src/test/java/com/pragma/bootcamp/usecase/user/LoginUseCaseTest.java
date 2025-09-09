package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.auth.gateways.TokenGateway;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.LoginAttemptGateway;
import com.pragma.bootcamp.model.gateways.PasswordEncryptionGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.usecase.auth.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

    private final String email = "test@example.com";
    private final String password = "password";
    private final User user = User.builder()
            .email(email)
            .password("hashedpassword")

            .build();
    private UserRepository userRepository;
    private PasswordEncryptionGateway passwordGateway;
    private TokenGateway tokenGateway;
    private LoginAttemptGateway loginAttemptGateway;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordGateway = mock(PasswordEncryptionGateway.class);
        tokenGateway = mock(TokenGateway.class);
        loginAttemptGateway = mock(LoginAttemptGateway.class);
        loginUseCase = new LoginUseCase(userRepository, passwordGateway, tokenGateway, loginAttemptGateway);
    }

    @Test
    void login_successful() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(passwordGateway.matches(password, user.getPassword())).thenReturn(Mono.just(true));
        when(tokenGateway.generateToken(user)).thenReturn(Mono.just("mock-token"));

        StepVerifier.create(loginUseCase.login(email, password))
                .expectNext("mock-token")
                .verifyComplete();
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.login(email, password))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
                })
                .verify();
    }

    @Test
    void login_incorrectPassword() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(passwordGateway.matches(password, user.getPassword())).thenReturn(Mono.just(false));

        StepVerifier.create(loginUseCase.login(email, password))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
                })
                .verify();
    }

    @Test
    void loginWithBlock_userIsBlocked() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(loginAttemptGateway.isUserBlocked(email)).thenReturn(Mono.just(true));

        StepVerifier.create(loginUseCase.loginWithBlock(email, password))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.USER_IS_BLOCKED;
                })
                .verify();
    }

    @Test
    void loginWithBlock_successful() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(loginAttemptGateway.isUserBlocked(email)).thenReturn(Mono.just(false));
        when(passwordGateway.matches(password, user.getPassword())).thenReturn(Mono.just(true));
        when(loginAttemptGateway.clearAttempts(email)).thenReturn(Mono.empty());
        when(tokenGateway.generateToken(user)).thenReturn(Mono.just("mock-token"));

        StepVerifier.create(loginUseCase.loginWithBlock(email, password))
                .expectNext("mock-token")
                .verifyComplete();
    }

    @Test
    void loginWithBlock_failedPassword() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(loginAttemptGateway.isUserBlocked(email)).thenReturn(Mono.just(false));
        when(passwordGateway.matches(password, user.getPassword())).thenReturn(Mono.just(false));
        when(loginAttemptGateway.recordFailedAttempt(email)).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.loginWithBlock(email, password))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
                })
                .verify();
    }
}
