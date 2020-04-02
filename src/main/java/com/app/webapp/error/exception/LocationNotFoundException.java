package com.app.webapp.error.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException() {
    }

    public LocationNotFoundException(String message) {
        super(message);
    }
}
