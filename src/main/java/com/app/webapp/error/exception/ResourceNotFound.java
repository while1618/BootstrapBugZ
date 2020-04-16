package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class ResourceNotFound extends RuntimeException {
    private String domain;

    public ResourceNotFound(String message) {
        super(message);
    }

    public ResourceNotFound(String message, String domain) {
        super(message);
        this.domain = domain;
    }
}
