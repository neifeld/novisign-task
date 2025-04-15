package com.nvs.task.slideshow.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuration class for initializing R2DBC test containers.
 * This class provides the necessary setup for using TestContainers
 * with R2DBC in a testing environment, specifically for PostgreSQL.
 */
@TestConfiguration
public class R2dbcTestContainersInitializer {

    /**
     * Creates and starts a PostgreSQL container for testing purposes.
     * This method initializes a PostgreSQL database container using TestContainers,
     * configures it with predefined settings, and sets up system properties for R2DBC connection.
     *
     * @return A started PostgreSQLContainer instance that can be used for testing.
     * The container is configured with a database named "slideshowdb",
     * username "test", and password "test".
     */
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("slideshowdb")
                .withUsername("test")
                .withPassword("test");
        container.start();

        // Set system properties for R2DBC to use
        System.setProperty("spring.r2dbc.url",
                String.format("r2dbc:postgresql://%s:%d/%s",
                        container.getHost(),
                        container.getFirstMappedPort(),
                        container.getDatabaseName()));
        System.setProperty("spring.r2dbc.username", container.getUsername());
        System.setProperty("spring.r2dbc.password", container.getPassword());

        return container;
    }
}