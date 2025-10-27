package com.netflixtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to return an HTTP 404 Not Found status when a resource
 * (User, Movie, History Record) is requested but does not exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // This tells Spring to map this exception to 404
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}