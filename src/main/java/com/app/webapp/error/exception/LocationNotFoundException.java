package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class LocationNotFoundException extends RuntimeException {
    private String domain = "location.notFound";

    public LocationNotFoundException() {

    }

    public LocationNotFoundException(String message) {
        super(message);
    }
}
