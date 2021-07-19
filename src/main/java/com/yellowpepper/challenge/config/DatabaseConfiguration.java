package com.yellowpepper.challenge.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseConfiguration {
    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(new ClassPathResource("1-ddl.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("2-dml.sql"));
        initializer.setDatabasePopulator(resourceDatabasePopulator);

        return initializer;
    }
}