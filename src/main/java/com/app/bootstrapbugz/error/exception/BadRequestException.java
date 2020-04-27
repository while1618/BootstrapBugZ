package com.app.bootstrapbugz.error.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String domain;

    public BadRequestException(String message, String domain) {
        super(message);
        this.domain = domain;
    }
}
