package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.gateways.LoginAttemptGateway;
import com.pragma.bootcamp.usecase.auth.ValidateLoginAttemptUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ValidateLoginAttemptUseCaseTest {

    private LoginAttemptGateway loginAttemptGateway;
    private ValidateLoginAttemptUseCase validateLoginAttemptUseCase;

    @BeforeEach
    void setUp() {
        loginAttemptGateway = Mockito.mock(LoginAttemptGateway.class);
        validateLoginAttemptUseCase = new ValidateLoginAttemptUseCase(loginAttemptGateway);
    }

    @Test
    void validateUser_WhenUserIsNotBlocked_ShouldReturnTrue() {
        String username = "user@example.com";
        Mockito.when(loginAttemptGateway.isUserBlocked(username))
                .thenReturn(Mono.just(false));

        Mono<Boolean> result = validateLoginAttemptUseCase.validateUser(username);

        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    void validateUser_WhenUserIsBlocked_ShouldReturnFalse() {
        String username = "blocked@example.com";
        Mockito.when(loginAttemptGateway.isUserBlocked(username))
                .thenReturn(Mono.just(true));

        Mono<Boolean> result = validateLoginAttemptUseCase.validateUser(username);

        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @Test
    void handleFailedLogin_ShouldRecordFailedAttempt() {
        String username = "user@example.com";
        Mockito.when(loginAttemptGateway.recordFailedAttempt(username))
                .thenReturn(Mono.empty());

        Mono<Void> result = validateLoginAttemptUseCase.handleFailedLogin(username);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        Mockito.verify(loginAttemptGateway, Mockito.only()).recordFailedAttempt(username);
    }

    @Test
    void handleSuccessfulLogin_ShouldClearAttempts() {
        String username = "user@example.com";
        Mockito.when(loginAttemptGateway.clearAttempts(username))
                .thenReturn(Mono.empty());

        Mono<Void> result = validateLoginAttemptUseCase.handleSuccessfulLogin(username);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        Mockito.verify(loginAttemptGateway, Mockito.only()).clearAttempts(username);
    }

    @Test
    void validateUser_WhenGatewayFails_ShouldPropagateError() {
        String username = "error@example.com";
        Exception exception = new RuntimeException("Database connection failed");
        Mockito.when(loginAttemptGateway.isUserBlocked(username))
                .thenReturn(Mono.error(exception));

        Mono<Boolean> result = validateLoginAttemptUseCase.validateUser(username);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}