package com.project.PJA.exception;

// 404 NotFound
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
