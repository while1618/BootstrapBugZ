package com.app.bootstrapbugz.error.exception;

import com.app.bootstrapbugz.error.ErrorDomain;
import lombok.Getter;

@Getter
public class ResourceNotFound extends RuntimeException {
    private final ErrorDomain domain;

    public ResourceNotFound(String message, ErrorDomain domain) {
        super(message);
        this.domain = domain;
    }
}
