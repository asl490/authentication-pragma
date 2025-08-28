package com.pragma.bootcamp.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.pragma.bootcamp.model.gateways.TransactionalGateway;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepository loanTypeRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public TransactionalGateway transactionalGateway() {
            return mock(TransactionalGateway.class);
        }

        @Bean
        public UserUseCase UserUseCase() {
            return new UserUseCase();
        }
    }

    static class UserUseCase {
        public String execute() {
            return "UserUseCase Test";
        }
    }
}