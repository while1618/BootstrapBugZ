package com.app.webapp.error.exception;

public class NotValidVerificationTokenException extends RuntimeException {
    public NotValidVerificationTokenException() {
    }

    public NotValidVerificationTokenException(String message) {
        super(message);
    }
}
