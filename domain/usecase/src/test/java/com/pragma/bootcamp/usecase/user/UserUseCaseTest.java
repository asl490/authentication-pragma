package com.pragma.bootcamp.usecase.user;

import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.gateways.PasswordEncryptionGateway;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.model.user.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.mockStatic;

class UserUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncryptionGateway passwordEncryptionGateway;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncryptionGateway = Mockito.mock(PasswordEncryptionGateway.class);
        userUseCase = new UserUseCase(userRepository, passwordEncryptionGateway);
    }

    @Test
    void getAll_ShouldReturnUserFlux() {
        User user1 = User.builder().id("1").email("a@example.com").document("123").build();
        User user2 = User.builder().id("2").email("b@example.com").document("456").build();

        Mockito.when(userRepository.getAll())
                .thenReturn(Flux.just(user1, user2));

        Flux<User> result = userUseCase.getAll();

        StepVerifier.create(result)
                .expectNext(user1, user2)
                .expectComplete()
                .verify();
    }

    @Test
    void findById_ValidId_UserExists_ShouldReturnUser() {
        String userId = "1";
        User user = User.builder().id(userId).email("a@example.com").build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.findById(userId);

        StepVerifier.create(result)
                .expectNext(user)
                .expectComplete()
                .verify();
    }

    @Test
    void findById_NullId_ShouldThrowIdNullError() {
        Mono<User> result = userUseCase.findById(null);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }

    @Test
    void findById_UserNotFound_ShouldThrowUserNotFound() {
        String userId = "999";

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findById(userId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }

    @Test
    void findByDocument_ValidDocument_UserExists_ShouldReturnUser() {
        String document = "12345678";
        User user = User.builder().id("1").document(document).build();

        Mockito.when(userRepository.findByDocument(document))
                .thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.findByDocument(document);

        StepVerifier.create(result)
                .expectNext(user)
                .expectComplete()
                .verify();
    }

    @Test
    void findByDocument_NullOrEmptyDocument_ShouldThrowInvalidDocument() {
        StepVerifier.create(userUseCase.findByDocument(null))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();

        StepVerifier.create(userUseCase.findByDocument(""))
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }

    @Test
    void findByDocument_NotFound_ShouldThrowUserNotFound() {
        String document = "12345678";

        Mockito.when(userRepository.findByDocument(document))
                .thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findByDocument(document);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }

    @Test
    void create_ValidUser_ShouldEncodePasswordAndCreate() {
        User user = User.builder()
                .id("1")
                .email("a@example.com")
                .document("123")
                .password("plainPassword")
                .birthDate(LocalDate.parse("2000-01-01"))
                .salary(BigDecimal.valueOf(100000))
                .build();

        User userWithEncodedPassword = user.toBuilder()
                .password("encodedPassword")
                .build();

        try (MockedStatic<UserValidation> mockedValidation = mockStatic(UserValidation.class)) {

            // ✅ Usa ArgumentMatcher para comparar por contenido, no por referencia
            mockedValidation.when(() -> UserValidation.validate(Mockito.argThat(u ->
                    u.getEmail().equals("a@example.com") &&
                            u.getDocument().equals("123") &&
                            u.getPassword().equals("plainPassword")
            ))).thenReturn(Mono.just(user));

            Mockito.when(userRepository.findByEmail("a@example.com"))
                    .thenReturn(Mono.empty());

            Mockito.when(userRepository.findByDocument("123"))
                    .thenReturn(Mono.empty());

            Mockito.when(passwordEncryptionGateway.encode("plainPassword"))
                    .thenReturn(Mono.just("encodedPassword"));

            Mockito.when(userRepository.create(Mockito.any(User.class)))
                    .thenReturn(Mono.just(userWithEncodedPassword));

            Mono<User> result = userUseCase.create(user);

            StepVerifier.create(result)
                    .expectNextMatches(savedUser ->
                            savedUser.getId().equals("1") &&
                                    savedUser.getEmail().equals("a@example.com") &&
                                    savedUser.getPassword().equals("encodedPassword")
                    )
                    .expectComplete()
                    .verify();
        }
    }

    @Test
    void create_InvalidUser_ShouldFailValidation() {
        User user = User.builder().email("invalid-email")
                .birthDate(LocalDate.parse("2000-01-01"))
                .salary(BigDecimal.valueOf(100000)).build();

        Mono<User> result = userUseCase.create(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();

        Mockito.verify(userRepository, Mockito.never()).findByEmail(Mockito.any());
    }

    @Test
    void create_EmailDuplicated_ShouldThrowError() {
        User user = User.builder()
                .email("a@example.com")
                .document("123")
                .password("pass")
                .birthDate(LocalDate.parse("2000-01-01"))
                .salary(BigDecimal.valueOf(100000))
                .build();

        User existingUser = User.builder().id("2").email("a@example.com").build();

        Mockito.when(UserValidation.validate(user))
                .thenReturn(Mono.just(user));

        Mockito.when(userRepository.findByEmail("a@example.com"))
                .thenReturn(Mono.just(existingUser));

        Mono<User> result = userUseCase.create(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();

        Mockito.verify(userRepository, Mockito.never()).findByDocument(Mockito.any());
    }

    @Test
    void create_DocumentDuplicated_ShouldThrowError() {
        User user = User.builder()
                .email("a@example.com")
                .document("123")
                .password("pass")
                .birthDate(LocalDate.parse("2000-01-01"))
                .salary(BigDecimal.valueOf(100000))
                .build();

        Mockito.when(UserValidation.validate(user))
                .thenReturn(Mono.just(user));

        Mockito.when(userRepository.findByEmail("a@example.com"))
                .thenReturn(Mono.empty());

        Mockito.when(userRepository.findByDocument("123"))
                .thenReturn(Mono.just(User.builder().id("2").document("123").build()));

        Mono<User> result = userUseCase.create(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }

    @Test
    void delete_UserExists_ShouldDelete() {
        String userId = "1";
        User user = User.builder().id(userId).build();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Mono.just(user));

        Mockito.when(userRepository.delete(userId))
                .thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.delete(userId);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void delete_UserNotFound_ShouldThrowError() {
        String userId = "999";

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.delete(userId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof UserValidationException;
                })
                .verify();
    }
}