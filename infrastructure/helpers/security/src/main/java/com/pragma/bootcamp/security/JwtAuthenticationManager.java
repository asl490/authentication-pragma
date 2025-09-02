package com.pragma.bootcamp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return jwtProvider.validateToken(authToken)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or expired JWT token")))
                .then(jwtProvider.getClaimsFromToken(authToken))
                .map(this::buildAuthenticationFromClaims)
                .onErrorMap(JwtException.class, ex ->
                        new BadCredentialsException("Invalid JWT token", ex))
                .onErrorMap(ex ->
                        new BadCredentialsException("Authentication failed", ex));
    }

    private Authentication buildAuthenticationFromClaims(Claims claims) {
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

}
