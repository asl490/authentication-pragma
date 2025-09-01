package com.pragma.bootcamp.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return Mono.just(jwtProvider.validateToken(authToken))
                .filter(isValid -> isValid)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or expired JWT token")))
                .map(isValid -> {
                    Claims claims = jwtProvider.getClaimsFromToken(authToken);
                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                });
    }
}
