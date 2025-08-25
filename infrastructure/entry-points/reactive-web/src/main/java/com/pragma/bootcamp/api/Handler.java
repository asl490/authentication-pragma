package com.pragma.bootcamp.api;

import com.pragma.bootcamp.api.dto.ErrorResponse;
import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.api.mapper.UserRestMapper;
import com.pragma.bootcamp.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserRestMapper userRestMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        log.trace("Received request to save a new user.");
        return serverRequest.bodyToMono(UserCreateDTO.class)
                .doOnNext(userDto -> log.trace("Request body: {}", userDto))
                .map(userRestMapper::toUser)
                .flatMap(userUseCase::create)
                .map(userRestMapper::toUserDTO)
                .flatMap(savedUserDto -> {
                    log.info("Successfully saved user with ID: {}", savedUserDto.getId());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedUserDto);
                });
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
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
