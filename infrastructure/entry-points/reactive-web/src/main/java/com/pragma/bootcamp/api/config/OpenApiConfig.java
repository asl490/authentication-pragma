package com.pragma.bootcamp.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bootcamp Pragma API")
                        .version("1.0.0")
                        .description("Documentación de la API del bootcamp de Pragma."));
    }
}
