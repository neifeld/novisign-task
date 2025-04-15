package com.nvs.task.slideshow.exception;

import java.util.List;

/**
 * Custom exception class for handling validation errors.
 * This class extends RuntimeException and provides additional functionality
 * for storing and retrieving validation error messages.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public ValidationException(String message) {
        super(message);
        this.errors = null;
    }

    /**
     * Constructs a new ValidationException with a formatted message combining resource type and message.
     *
     * @param resourceType the type of resource that failed validation
     * @param message      the detail message describing the validation error
     */
    public ValidationException(String resourceType, String message) {
        super(String.format("%s: %s", resourceType, message));
        this.errors = null;
    }

    /**
     * Constructs a new ValidationException for a resource not found scenario.
     *
     * @param resourceType the type of resource that was not found
     * @param id           the identifier of the resource that was not found
     */
    public ValidationException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
        this.errors = null;
    }

    /**
     * Constructs a new ValidationException with a formatted message and a cause.
     *
     * @param resourceType the type of resource that failed validation
     * @param message      the detail message describing the validation error
     * @param cause        the cause (which is saved for later retrieval by the getCause() method)
     */
    public ValidationException(String resourceType, String message, Throwable cause) {
        super(String.format("%s: %s", resourceType, message), cause);
        this.errors = null;
    }

    /**
     * Constructs a new ValidationException with a message and a list of error messages.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param errors  a list of specific error messages related to the validation failure
     */
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Returns the list of error messages associated with this exception.
     *
     * @return a List of String containing error messages, or null if no specific errors were set
     */
    public List<String> getErrors() {
        return errors;
    }
}