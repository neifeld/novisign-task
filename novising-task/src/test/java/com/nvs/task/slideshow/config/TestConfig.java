package com.nvs.task.slideshow.config;

import com.nvs.task.slideshow.repository.ImageRepository;
import com.nvs.task.slideshow.repository.SlideshowRepository;
import com.nvs.task.slideshow.service.KafkaProducerService;
import com.nvs.task.slideshow.service.SlideshowService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * Test configuration class for setting up mock beans and preventing database initialization.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates a null ConnectionFactoryInitializer to prevent database initialization.
     *
     * @return null ConnectionFactoryInitializer
     */
    @Bean
    @Primary
    public ConnectionFactoryInitializer initializer() {
        // Return null or a mock to prevent database initialization
        return null;
    }

    /**
     * Creates a mock SlideshowRepository bean.
     *
     * @return A mock SlideshowRepository
     */
    @Bean
    @Primary
    public SlideshowRepository slideshowRepository() {
        return Mockito.mock(SlideshowRepository.class);
    }

    /**
     * Creates a mock ImageRepository bean.
     *
     * @return A mock ImageRepository
     */
    @Bean
    public ImageRepository imageRepository() {
        return Mockito.mock(ImageRepository.class);
    }

    /**
     * Creates a mock DatabaseClient bean.
     *
     * @return A mock DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient() {
        return Mockito.mock(DatabaseClient.class);
    }

    /**
     * Creates a mock KafkaTemplate bean for String key-value pairs.
     *
     * @return A mock KafkaTemplate<String, String>
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    /**
     * Creates a mock KafkaProducerService bean.
     *
     * @return A mock KafkaProducerService
     */
    @Bean
    public KafkaProducerService kafkaProducerService() {
        return Mockito.mock(KafkaProducerService.class);
    }

    /**
     * Creates a mock SlideshowService bean.
     *
     * @return A mock SlideshowService
     */
    @Bean
    @Primary
    public SlideshowService slideshowService() {
        return Mockito.mock(SlideshowService.class);
    }
}