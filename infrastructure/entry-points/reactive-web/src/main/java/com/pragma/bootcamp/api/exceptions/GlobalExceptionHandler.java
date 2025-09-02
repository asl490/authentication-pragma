package com.pragma.bootcamp.api.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.debug("Handling exception: {}", ex.getClass().getSimpleName(), ex.getMessage());

        ErrorStrategy strategy = ErrorStrategy.findStrategy(ex.getClass());
        return strategy.handle(exchange, ex, objectMapper, messageSource)
                .doOnError(error -> log.error("Error handling exception", error.getMessage()))
                .onErrorResume(error -> ErrorStrategy.DEFAULT.handle(exchange, ex, objectMapper, messageSource));
    }
}