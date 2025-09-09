package com.pragma.bootcamp.security;

import com.pragma.bootcamp.model.auth.gateways.TokenGateway;
import com.pragma.bootcamp.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider implements TokenGateway {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public Mono<String> generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        claims.put("document", user.getDocument());

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getKey())
                .compact();
        return Mono.just(token);
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Mono<Claims> getClaimsFromToken(String token) {

        return Mono.fromCallable(() -> Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload())
                .onErrorMap(this::mapJwtException);
    }

    private Throwable mapJwtException(Throwable ex) {
        String message = switch (ex) {
            case SecurityException ignored -> {
                log.debug("Invalid JWT signature for token");
                yield "Invalid JWT signature";
            }
            case ExpiredJwtException ignored -> {
                log.debug("Expired JWT token");
                yield "Token has expired";
            }
            case UnsupportedJwtException ignored -> {
                log.debug("Unsupported JWT token");
                yield "Unsupported JWT token";
            }
            case MalformedJwtException ignored -> {
                log.debug("Malformed JWT token");
                yield "Malformed JWT token";
            }
            case IllegalArgumentException ignored -> {
                log.debug("Empty or invalid JWT token");
                yield "Invalid JWT token";
            }
            case SignatureException ignored -> {
                log.debug("JWT signature does not match locally computed signature {}", ex.getMessage());
                yield "JWT signature does not match locally computed signature";
            }
            default -> {
                log.warn("Unexpected JWT parsing error {}", ex.getMessage());
                yield "JWT processing error";
            }
        };

        return new JwtException(message, ex);
    }

    public Mono<Boolean> validateToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> true)
                .onErrorResume(e -> {
                    log.warn("Token validation failed: {}", e.getMessage());
                    return Mono.just(false);
                });
    }
}
