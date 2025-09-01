package com.pragma.bootcamp;

import com.pragma.bootcamp.api.RouterRest;
import com.pragma.bootcamp.api.config.OpenApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import com.pragma.bootcamp.security.SecurityConfig;

@SpringBootApplication
@ConfigurationPropertiesScan
@Import({RouterRest.class, OpenApiConfig.class, SecurityConfig.class})
@EnableAutoConfiguration(exclude = {ReactiveSecurityAutoConfiguration.class,
        ReactiveUserDetailsServiceAutoConfiguration.class})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
