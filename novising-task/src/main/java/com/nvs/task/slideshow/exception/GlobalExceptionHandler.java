package com.nvs.task.slideshow.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvs.task.slideshow.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for managing and formatting error responses in the application.
 * This handler intercepts exceptions thrown during request processing and converts them
 * into standardized JSON error responses.
 */
@Component
@Order(-2) // Higher precedence than DefaultErrorWebExceptionHandler
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new GlobalExceptionHandler with the provided ObjectMapper.
     *
     * @param objectMapper The ObjectMapper used for JSON serialization.
     */
    @Autowired
    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Handles exceptions by creating and writing a standardized error response.
     *
     * @param exchange The ServerWebExchange which provides access to the HTTP request and response.
     * @param ex       The Throwable that was thrown during request processing.
     * @return A Mono&lt;Void&gt; representing the asynchronous completion of the error handling process.
     */
    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        // Set response headers
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Determine HTTP status and error message
        HttpStatus status;
        String errorMessage;

        if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = ex.getMessage();
        } else if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = ex.getMessage();
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            errorMessage = responseStatusException.getReason();
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "An unexpected error occurred: " + ex.getMessage();
        }

        // Set the HTTP status
        exchange.getResponse().setStatusCode(status);

        // Create error response
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                exchange.getRequest().getPath().value()
        );

        // Convert to JSON and write to response
        DataBuffer dataBuffer;
        try {
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
        } catch (JsonProcessingException e) {
            dataBuffer = bufferFactory.wrap(("Error processing response: " + e.getMessage()).getBytes());
        }

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}