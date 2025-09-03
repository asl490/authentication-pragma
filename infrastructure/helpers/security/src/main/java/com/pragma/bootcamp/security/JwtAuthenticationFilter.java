//package com.pragma.bootcamp.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter implements WebFilter {
//
//    private static final String BEARER = "Bearer ";
//
//    private final ReactiveAuthenticationManager authenticationManager;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader != null && authHeader.startsWith(BEARER)) {
//            String authToken = authHeader.substring(BEARER.length());
//            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
//
//            return authenticationManager.authenticate(auth)
//                    .flatMap(authentication ->
//                            chain.filter(exchange)
//                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
//                    )
//                    .switchIfEmpty(chain.filter(exchange)); // Si no autenticó, sigue sin contexto
//        }
//
//        return chain.filter(exchange);
//    }
//}
