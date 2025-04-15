package com.nvs.task.slideshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Novisign Task application.
 * This class is annotated with @SpringBootApplication, which enables auto-configuration
 * and component scanning for a Spring Boot application.
 */
@SpringBootApplication
public class NovisingTaskApplication {

    /**
     * The main method which serves as the entry point for the application.
     * It uses SpringApplication to bootstrap and launch the Spring application.
     *
     * @param args Command line arguments passed to the application.
     *             These arguments can be used to override default configuration,
     *             set profiles, or pass other runtime parameters.
     */
    public static void main(String[] args) {
        SpringApplication.run(NovisingTaskApplication.class, args);
    }

}