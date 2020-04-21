package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class ResourceNotFound extends RuntimeException {
    private final String domain;

    public ResourceNotFound(String message, String domain) {
        super(message);
        this.domain = domain;
    }
}
