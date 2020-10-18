package com.app.bootstrapbugz.error.exception;

import com.app.bootstrapbugz.error.ErrorDomain;
import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final ErrorDomain domain;

    public ForbiddenException(String message, ErrorDomain domain) {
        super(message);
        this.domain = domain;
    }
}
