package com.pragma.bootcamp;

import com.pragma.bootcamp.api.RouterRest;
import com.pragma.bootcamp.api.config.OpenApiConfig;
import com.pragma.bootcamp.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@Import({RouterRest.class, OpenApiConfig.class, SecurityConfig.class})

public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
