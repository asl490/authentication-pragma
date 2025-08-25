package com.pragma.bootcamp.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("mysql")
@ConfigurationProperties(prefix = "adapters.r2dbc")
public record MysqlConnectionProperties(
                String host,
                Integer port,
                String database,
                String username,
                String password) {
}
