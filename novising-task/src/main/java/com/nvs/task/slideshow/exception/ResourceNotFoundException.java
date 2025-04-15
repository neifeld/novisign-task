package com.nvs.task.slideshow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * This exception is mapped to HTTP 404 (Not Found) status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message including the resource type and id.
     *
     * @param resourceType the type of resource that was not found
     * @param id           the id of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message including the resource type and a custom message.
     *
     * @param resourceType the type of resource that was not found
     * @param message      additional details about the exception
     */
    public ResourceNotFoundException(String resourceType, String message) {
        super(String.format("%s: %s", resourceType, message));
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message including the resource type,
     * a custom message, and a cause.
     *
     * @param resourceType the type of resource that was not found
     * @param message      additional details about the exception
     * @param cause        the cause of this exception
     */
    public ResourceNotFoundException(String resourceType, String message, Throwable cause) {
        super(String.format("%s: %s", resourceType, message), cause);
    }
}