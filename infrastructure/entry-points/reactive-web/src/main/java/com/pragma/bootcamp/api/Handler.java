package com.pragma.bootcamp.api;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pragma.bootcamp.api.dto.LoginRequestDTO;
import com.pragma.bootcamp.api.dto.LoginResponseDTO;
import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.api.exceptions.ErrorResponse;
import com.pragma.bootcamp.api.mapper.UserRestMapper;
import com.pragma.bootcamp.model.exception.AuthenticationException;
import com.pragma.bootcamp.usecase.auth.LoginUseCase;
import com.pragma.bootcamp.usecase.user.UserUseCase;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final LoginUseCase loginUseCase;
    private final UserRestMapper userRestMapper;
    private final Validator validator;

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserCreateDTO.class)
                .doOnNext(dto -> log.trace("Request body: {}", dto))
                .doOnNext(dto -> {
                    Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        Mono.error(new ConstraintViolationException(violations));
                    }
                })
                .map(userRestMapper::toUser)
                .flatMap(userUseCase::create)
                .map(userRestMapper::toUserDTO)
                .flatMap(savedUserDto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUserDto));
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

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAll().map(userRestMapper::toUserDTO), UserDTO.class);
    }

    public Mono<ServerResponse> listenDeleteUser(ServerRequest serverRequest) {
        String id = (serverRequest.pathVariable("id"));
        return userUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> listenFindByDocument(ServerRequest serverRequest) {
        String document = serverRequest.pathVariable("document");
        return userUseCase.findByDocument(document)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequestDTO.class)
                .doOnNext(dto -> {
                    Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        Mono.error(new ConstraintViolationException(violations));
                    }
                })
                .flatMap(loginRequest -> loginUseCase.login(loginRequest.getEmail(),
                        loginRequest.getPassword()))
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new LoginResponseDTO(token)))
                .onErrorResume(AuthenticationException.class, e -> ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.getErrorCode().getCode(),
                                e.getErrorCode().name(),
                                LocalDateTime.now())));
    }
}
