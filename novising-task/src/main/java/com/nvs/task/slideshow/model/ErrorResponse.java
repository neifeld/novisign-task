package com.nvs.task.slideshow.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ErrorResponse is a model class that represents the structure of an error response
 * returned by the API in case of an error or exception.
 * It includes fields such as timestamp, status, error, message, path, and details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    private String path;
    private List<String> details;

    /**
     * Default constructor for ErrorResponse.
     * Initializes the timestamp to the current date and time.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor for creating an ErrorResponse with basic error information.
     *
     * @param status  The HTTP status code of the error.
     * @param error   A short description of the error.
     * @param message A more detailed error message.
     * @param path    The path of the request that caused the error.
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Constructor for creating an ErrorResponse with additional error details.
     *
     * @param status  The HTTP status code of the error.
     * @param error   A short description of the error.
     * @param message A more detailed error message.
     * @param path    The path of the request that caused the error.
     * @param details A list of additional error details.
     */
    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this(status, error, message, path);
        this.details = details;
    }

    /**
     * Constructor for creating an ErrorResponse with all fields specified.
     *
     * @param status    The HTTP status code of the error.
     * @param error     A short description of the error.
     * @param message   A more detailed error message.
     * @param path      The path of the request that caused the error.
     * @param details   A list of additional error details.
     * @param timestamp The date and time when the error occurred.
     */
    public ErrorResponse(int status, String error, String message, String path, List<String> details, LocalDateTime timestamp) {
        this(status, error, message, path, details);
        this.timestamp = timestamp;
    }
}