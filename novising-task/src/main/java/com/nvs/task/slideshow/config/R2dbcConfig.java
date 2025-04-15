package com.nvs.task.slideshow.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;

/**
 * Configuration class for R2DBC database connections.
 * This class sets up the connection factory for different database types (H2, TestContainers, PostgreSQL)
 * and configures the connection pool.
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "com.nvs.task.slideshow.repository")
@EnableTransactionManagement
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String url;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Value("${spring.r2dbc.pool.initial-size:5}")
    private int initialSize;

    @Value("${spring.r2dbc.pool.max-size:20}")
    private int maxSize;

    @Value("${spring.r2dbc.pool.max-idle-time:30m}")
    private String maxIdleTime;

    /**
     * Creates and configures a ConnectionFactory based on the database type specified in the URL.
     * Supports H2 (for tests), TestContainers, and PostgreSQL databases.
     * For PostgreSQL, it sets up a connection pool with the specified configuration.
     *
     * @return A ConnectionFactory instance configured for the appropriate database type.
     * For PostgreSQL, returns a ConnectionPool instance.
     */
    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        // For H2 database in tests
        if (url.contains("r2dbc:h2:")) {
            return ConnectionFactoryBuilder.withUrl(url)
                    .username(username)
                    .password(password)
                    .build();
        }

        // For TestContainers
        if (url.contains("r2dbc:tc:")) {
            return ConnectionFactoryBuilder.withUrl(url)
                    .username(username)
                    .password(password)
                    .build();
        }

        // Regular PostgreSQL connection
        String cleanUrl = url.replace("r2dbc:postgresql://", "");
        String[] parts = cleanUrl.split("/");
        String hostPort = parts[0];
        String database = parts[1];

        String[] hostPortParts = hostPort.split(":");
        String host = hostPortParts[0];
        int port = Integer.parseInt(hostPortParts[1]);

        // Create PostgreSQL connection configuration
        PostgresqlConnectionConfiguration pgConfig = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Create connection factory with the configuration
        PostgresqlConnectionFactory connectionFactory = new PostgresqlConnectionFactory(pgConfig);

        // Create connection pool configuration
        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder()
                .connectionFactory(connectionFactory)
                .initialSize(initialSize)
                .maxSize(maxSize)
                .maxIdleTime(Duration.parse("PT" + maxIdleTime.toUpperCase()))
                .validationQuery("SELECT 1")
                .build();

        // Create and return the connection pool
        return new ConnectionPool(poolConfig);
    }

    /**
     * Creates a ReactiveTransactionManager for managing reactive transactions.
     *
     * @param connectionFactory The ConnectionFactory to be used for creating the transaction manager.
     * @return A ReactiveTransactionManager instance configured with the provided ConnectionFactory.
     */
    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}