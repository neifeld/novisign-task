package com.nvs.task.slideshow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for handling bad request errors.
 * This exception is thrown when a client sends an invalid request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadRequestException with a formatted message including the resource type and a custom message.
     *
     * @param resourceType the type of resource that caused the exception
     * @param message      additional details about the exception
     */
    public BadRequestException(String resourceType, String message) {
        super(String.format("%s: %s", resourceType, message));
    }

    /**
     * Constructs a new BadRequestException with a formatted message including the resource type and its ID.
     *
     * @param resourceType the type of resource that caused the exception
     * @param id           the ID of the resource that was not found
     */
    public BadRequestException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }
}