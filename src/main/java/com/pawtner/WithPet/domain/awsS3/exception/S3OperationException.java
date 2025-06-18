package com.pawtner.WithPet.domain.awsS3.exception;

public class S3OperationException extends RuntimeException {
    public S3OperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
