package com.cityguide.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException attraction(Long id) {
        return new ResourceNotFoundException("Достопримечательность с id=" + id + " не найдена");
    }
}
