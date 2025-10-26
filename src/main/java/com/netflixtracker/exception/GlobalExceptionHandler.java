package com.netflixtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global Exception Handler to centralize error responses.
 * This ensures all exceptions (like IllegalArgumentException) return
 * clean HTTP status codes (like 404 or 400) and structured JSON bodies.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles the custom exceptions thrown in your Service layer
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found or Bad Request");
        body.put("message", ex.getMessage());
        
        // This is primarily used for 'Resource Not Found' errors (404),
        // but can also cover 'Bad Request' (400) from invalid data/rating.
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    // You could add other handlers here, e.g., @ExceptionHandler(DataIntegrityViolationException.class)
    // to catch database foreign key errors and return a 409 Conflict.
}