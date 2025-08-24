package com.pragma.bootcamp.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.postgres.pool")
public record PostgreSQLConnectionPoolProperties(
        int initialSize,
        int maxSize,
        int maxIdleTime) {
}