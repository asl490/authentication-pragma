package com.pragma.bootcamp.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.api.mapper.UserRestMapper;
import com.pragma.bootcamp.usecase.user.UserUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserRestMapper userRestMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserCreateDTO.class)
                .doOnNext(System.out::println)
                .map(userRestMapper::toUser)
                .flatMap(userUseCase::create)
                .map(userRestMapper::toUserDTO)
                .flatMap(savedUserDto -> ServerResponse.ok()
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
