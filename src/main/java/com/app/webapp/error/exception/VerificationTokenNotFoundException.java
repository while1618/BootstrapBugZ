package com.app.webapp.error.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
    }

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
