package com.pragma.bootcamp.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String BEARER = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith(BEARER)) {
            token = token.substring(BEARER.length());
            Authentication authentication = new UsernamePasswordAuthenticationToken(token, token);
            return ReactiveSecurityContextHolder.getContext()
                    .defaultIfEmpty(SecurityContextHolder.createEmptyContext())
                    .flatMap(context -> {
                        context.setAuthentication(authentication);
                        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                    });
        }
        return chain.filter(exchange);
    }
}
