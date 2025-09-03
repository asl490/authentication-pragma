package com.pragma.bootcamp.api.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.bootcamp.model.exception.DataIntegrityViolationException;
import com.pragma.bootcamp.model.exception.NotFoundException;
import com.pragma.bootcamp.model.exception.UserValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
enum ErrorStrategy {

    VALIDATION(WebExchangeBindException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            WebExchangeBindException bindEx = (WebExchangeBindException) ex;
            List<String> errors = bindEx.getBindingResult().getFieldErrors().stream()
                    .map(fe -> String.format("%s: %s", fe.getField(),
                            Optional.ofNullable(fe.getDefaultMessage()).orElse("Error de validación")))
                    .collect(Collectors.toList());

            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), ex.getMessage(), errors,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse, objectMapper);
        }
    },

    NOT_FOUND(NotFoundException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.NOT_FOUND, errorResponse, objectMapper);
        }
    },

    DATA_INTEGRITY(DataIntegrityViolationException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            String constraintMessage = extractConstraintMessage(ex.getMessage());
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.CONFLICT.name(), ex.getMessage(),
                    List.of(constraintMessage), exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.CONFLICT, errorResponse, objectMapper);
        }

        private String extractConstraintMessage(String message) {
            if (message != null && message.contains("constraint")) {
                return "Violación de restricción de integridad de datos";
            }
            return "Error de integridad de datos";
        }
    },

    ACCESS_DENIED(AccessDeniedException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.FORBIDDEN.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
        }
    },

    TIMEOUT(TimeoutException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.REQUEST_TIMEOUT.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.REQUEST_TIMEOUT, errorResponse, objectMapper);
        }
    },
    CONSTRAINT_VIOLATION(ConstraintViolationException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ConstraintViolationException cvEx = (ConstraintViolationException) ex;
            List<String> errors = cvEx.getConstraintViolations().stream()
                    .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                    .collect(Collectors.toList());

            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), ex.getMessage(), errors,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage() + " {}", errors.toString());
            return writeResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse, objectMapper);
        }
    },
    USER_VALIDATION(UserValidationException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            UserValidationException uve = (UserValidationException) ex;

            String resolvedMessage = resolveMessage(uve, messageSource);
            log.warn("User validation error [{}]: {}", uve.getCode(), resolvedMessage);

            ErrorResponse errorResponse = buildErrorResponse(
                    uve.getCode(),
                    resolvedMessage,
                    null,
                    exchange.getRequest().getPath().value());

            return writeResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse, objectMapper);
        }

        private String resolveMessage(UserValidationException uve, MessageSource messageSource) {
            // Si ya tiene mensaje resuelto, usarlo
            if (uve.getResolvedMessage() != null && !uve.getResolvedMessage().equals(uve.getCode())) {
                return uve.getResolvedMessage();
            }

            // Sino, resolver desde MessageSource
            try {
                return messageSource.getMessage(uve.getCode(), null, "Mensaje no encontrado", Locale.getDefault());
            } catch (Exception e) {
                log.warn("Could not resolve message for code: {}", uve.getCode(), e);
                return "Error de validación de usuario";
            }
        }
    },

    DEFAULT(Throwable.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            log.warn("error {}", ex);
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(),
                    null, exchange.getRequest().getPath().value());

            return writeResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, objectMapper);
        }
    };

    // Mapa estático para búsqueda rápida O(1)
    private static final Map<Class<? extends Throwable>, ErrorStrategy> STRATEGY_MAP = Arrays
            .stream(ErrorStrategy.values())
            .filter(strategy -> strategy != DEFAULT)
            .collect(Collectors.toUnmodifiableMap(
                    strategy -> strategy.exceptionClass,
                    Function.identity()));

    public static ErrorStrategy findStrategy(Class<? extends Throwable> exceptionClass) {
        // Búsqueda exacta O(1)
        ErrorStrategy strategy = STRATEGY_MAP.get(exceptionClass);
        if (strategy != null) {
            return strategy;
        }

        // Búsqueda por jerarquía para subclases
        return STRATEGY_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(exceptionClass))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT);
    }

    // Métodos helper estáticos para reutilización
    protected static ErrorResponse buildErrorResponse(String code, String message,
                                                      List<String> details, String path) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(details)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    protected static Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status,
                                              ErrorResponse errorResponse, ObjectMapper objectMapper) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializing error response", e);
            return response.setComplete();
        }
    }

    private final Class<? extends Throwable> exceptionClass;

    public abstract Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                      MessageSource messageSource);
}
