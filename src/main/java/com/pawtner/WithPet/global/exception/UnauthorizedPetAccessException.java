package com.pawtner.WithPet.global.exception;

public class UnauthorizedPetAccessException extends RuntimeException {
    public UnauthorizedPetAccessException(String message) {
        super(message);
    }
}