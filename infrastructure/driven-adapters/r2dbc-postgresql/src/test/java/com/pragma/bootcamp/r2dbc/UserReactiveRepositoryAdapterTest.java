package com.pragma.bootcamp.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;

import com.pragma.bootcamp.model.exception.BusinessException;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User user;
    private UserEntity userEntity;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Jhon")
                .lastName("Doe")
                .birthDate(LocalDate.of(1997, 2, 2))
                .phone("98988787")
                .address("test address")
                .email(testEmail)
                // .password("hashedpassword")
                .salary(new BigDecimal(5000))
                // .roleId(testRoleId)
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .name("Jhon")
                .lastName("Doe")
                .birthDate(LocalDate.of(1997, 2, 2))
                .phone("98988787")
                .address("test address")
                .email(testEmail)
                // .password("hashedpassword")
                .salary(new BigDecimal(5000))
                // .roleId(testRoleId)
                .build();
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.getAll())
                .expectNext(user)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void update_ShouldUpdateUserSuccessfully() {
        // Arrange
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.update(user))
                .expectNext(user)
                .verifyComplete();

        verify(repository).save(userEntity);
    }

    @Test
    void delete_ShouldDeleteUserById() {
        // Arrange
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.delete(1L))
                .verifyComplete();

        verify(repository).deleteById(1L);
    }

    @Test
    void create_ShouldCreateUserSuccessfully() {
        // Arrange
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.create(user))
                .expectNext(user)
                .verifyComplete();

        verify(repository).save(userEntity);
    }

    @Test
    void create_ShouldThrowBusinessException_WhenDocumentAlreadyExists() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "users.document constraint violation");

        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.create(user))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(BusinessException.class);
                    assertThat(throwable.getMessage()).isEqualTo("The document is already registered.");
                })
                .verify();

        verify(repository).save(userEntity);
    }

    @Test
    void create_ShouldThrowBusinessException_WhenDocumentAlreadyExists_AlternativeConstraintName() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "users_document_key constraint violation");

        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.create(user))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(BusinessException.class);
                    assertThat(throwable.getMessage()).isEqualTo("The document is already registered.");
                })
                .verify();

        verify(repository).save(userEntity);
    }

    @Test
    void create_ShouldPropagateException_WhenOtherDataIntegrityViolationOccurs() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("other_constraint violation");

        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.create(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();

        verify(repository).save(userEntity);
    }

    @Test
    void create_ShouldPropagateOtherExceptions() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");

        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.create(user))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).save(userEntity);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        String email = "john.doe@example.com";
        when(repository.findByEmail(email)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectNext(user)
                .verifyComplete();

        verify(repository).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(repository.findByEmail(email)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .verifyComplete();

        verify(repository).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldHandleErrors() {
        // Arrange
        String email = "error@example.com";
        RuntimeException exception = new RuntimeException("Database error");
        when(repository.findByEmail(email)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail(email);
    }
}