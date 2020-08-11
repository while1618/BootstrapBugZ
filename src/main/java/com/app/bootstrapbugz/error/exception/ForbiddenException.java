package com.app.bootstrapbugz.error.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final String domain;

    public ForbiddenException(String message, String domain) {
        super(message);
        this.domain = domain;
    }
}
