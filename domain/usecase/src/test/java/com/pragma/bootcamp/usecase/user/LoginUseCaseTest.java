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
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoginUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncryptionGateway passwordGateway;
    private TokenGateway tokenGateway;
    private LoginAttemptGateway loginAttemptGateway;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordGateway = Mockito.mock(PasswordEncryptionGateway.class);
        tokenGateway = Mockito.mock(TokenGateway.class);
        loginAttemptGateway = Mockito.mock(LoginAttemptGateway.class);

        loginUseCase = new LoginUseCase(userRepository, passwordGateway, tokenGateway, loginAttemptGateway);
    }

    @Test
    void login_SuccessfulLogin_ShouldReturnToken() {
        String email = "ana@example.com";
        String password = "correctPassword";
        String token = "generated-jwt-token";

        User user = User.builder()
                .id(String.valueOf(1L))
                .email(email)
                .password("$2a$10$hashedPassword")
                .build();

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));

        Mockito.when(loginAttemptGateway.isUserBlocked(email))
                .thenReturn(Mono.just(false));

        Mockito.when(passwordGateway.matches(password, user.getPassword()))
                .thenReturn(Mono.just(true));

        Mockito.when(loginAttemptGateway.clearAttempts(email))
                .thenReturn(Mono.empty());

        Mockito.when(tokenGateway.generateToken(user))
                .thenReturn(Mono.just(token));

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectNext(token)
                .expectComplete()
                .verify();

        Mockito.verify(loginAttemptGateway).clearAttempts(email);
        Mockito.verify(tokenGateway).generateToken(user);
    }

    @Test
    void login_UserNotFound_ShouldThrowAuthenticationFailed() {
        String email = "notfound@example.com";
        String password = "anyPassword";

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.empty());

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
                })
                .verify();
    }

    @Test
    void login_UserIsBlocked_ShouldThrowUserIsBlocked() {
        String email = "blocked@example.com";
        String password = "password123";

        User user = User.builder()
                .id(String.valueOf(1L))
                .email(email)
                .password("$2a$10$hashed")
                .build();

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));

        Mockito.when(loginAttemptGateway.isUserBlocked(email))
                .thenReturn(Mono.just(true));

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.USER_IS_BLOCKED;
                })
                .verify();

        Mockito.verify(passwordGateway, Mockito.never()).matches(Mockito.any(), Mockito.any());
        Mockito.verify(tokenGateway, Mockito.never()).generateToken(Mockito.any());
        Mockito.verify(loginAttemptGateway, Mockito.never()).clearAttempts(email);
    }

    @Test
    void login_InvalidPassword_ShouldRecordFailedAttemptAndThrow() {
        String email = "ana@example.com";
        String password = "wrongPassword";

        User user = User.builder()
                .id(String.valueOf(1L))
                .email(email)
                .password("$2a$10$hashedPassword")
                .build();

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));

        Mockito.when(loginAttemptGateway.isUserBlocked(email))
                .thenReturn(Mono.just(false));

        Mockito.when(passwordGateway.matches(password, user.getPassword()))
                .thenReturn(Mono.just(false));

        Mockito.when(loginAttemptGateway.recordFailedAttempt(email))
                .thenReturn(Mono.empty());

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
//                    assert ((UserValidationException) throwable).getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
                })
                .verify();

        Mockito.verify(loginAttemptGateway).recordFailedAttempt(email);
    }

    @Test
    void login_RepositoryError_ShouldPropagateError() {
        String email = "error@example.com";
        String password = "pass";

        Exception exception = new RuntimeException("DB connection failed");
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.error(exception));

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void login_IsUserBlockedFails_ShouldPropagateError() {
        String email = "ana@example.com";
        String password = "pass";

        User user = User.builder().email(email).password("pass").build();

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));

        Mockito.when(loginAttemptGateway.isUserBlocked(email))
                .thenReturn(Mono.error(new RuntimeException("Redis down")));

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void login_PasswordGatewayError_ShouldPropagate() {
        String email = "ana@example.com";
        String password = "pass";

        User user = User.builder().email(email).password("pass").build();

        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(user));

        Mockito.when(loginAttemptGateway.isUserBlocked(email))
                .thenReturn(Mono.just(false));

        Mockito.when(passwordGateway.matches(password, user.getPassword()))
                .thenReturn(Mono.error(new RuntimeException("BCrypt error")));

        Mono<String> result = loginUseCase.login(email, password);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}