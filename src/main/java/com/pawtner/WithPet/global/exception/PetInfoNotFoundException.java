package com.pawtner.WithPet.global.exception;

public class PetInfoNotFoundException extends RuntimeException {
    public PetInfoNotFoundException(String message) {
        super(message);
    }
}