package com.pragma.bootcamp.config;

import com.pragma.bootcamp.application.usecase.UserUseCaseImpl;
import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.usecase.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public UserUseCase userUseCase(UserRepository userRepository, TransactionalGateway transactionalGateway) {
        return new UserUseCaseImpl(userRepository, transactionalGateway);
    }
}
