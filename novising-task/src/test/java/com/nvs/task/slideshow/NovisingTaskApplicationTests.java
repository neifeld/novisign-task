package com.nvs.task.slideshow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class for the NovisingTask application.
 * This class is responsible for testing the Spring Boot application context loading.
 * It uses an in-memory H2 database for testing purposes.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password=",
        "spring.sql.init.mode=embedded"
})
class NovisingTaskApplicationTests {

    /**
     * Tests if the application context loads successfully.
     * This method doesn't perform any specific assertions but will fail if the context fails to load.
     */
    @Test
    void contextLoads() {
    }
}