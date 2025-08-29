package com.pragma.bootcamp.api;

import com.pragma.bootcamp.api.dto.ErrorResponse;
import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.api.mapper.UserRestMapper;
import com.pragma.bootcamp.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserRestMapper userRestMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        log.trace("Received request to save a new user.");
        return serverRequest.bodyToMono(UserCreateDTO.class)
                .doOnNext(userDto -> log.trace("Request body: {}", userDto))
                .doOnNext(this::validate) // Validar antes de procesar
                .map(userRestMapper::toUser)
                .flatMap(userUseCase::create)
                .map(userRestMapper::toUserDTO)
                .flatMap(savedUserDto -> {
                    log.info("Successfully saved user with ID: {}", savedUserDto.getId());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedUserDto);
                })
                .onErrorResume(ConstraintViolationException.class, this::handleValidationException);
    }

    public Mono<ServerResponse> listenUpdateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .map(userRestMapper::toUser)
                .flatMap(userUseCase::update)
                .map(userRestMapper::toUserDTO)
                .flatMap(savedUserDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUserDto));
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAll().map(userRestMapper::toUserDTO), UserDTO.class);
    }

    public Mono<ServerResponse> listenDeleteUser(ServerRequest serverRequest) {
        UUID id = UUID.fromString(serverRequest.pathVariable("id"));
        return userUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> listenFindByDocument(ServerRequest serverRequest) {
        String document = serverRequest.pathVariable("document");
        return userUseCase.findByDocument(document)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private void validate(UserCreateDTO userDto) {
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Mono<ServerResponse> handleValidationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        log.error("Validation errors: {}", errors);
        ErrorResponse errorResponse = ErrorResponse.builder()

                .code(HttpStatus.BAD_REQUEST.name())
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}
