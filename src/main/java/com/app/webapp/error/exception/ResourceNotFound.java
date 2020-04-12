package com.app.webapp.error.exception;

import com.app.webapp.error.ErrorDomains;
import lombok.Getter;

@Getter
public class ResourceNotFound extends RuntimeException {
    private ErrorDomains domain;

    public ResourceNotFound(String message, ErrorDomains domain) {
        super(message);
        this.domain = domain;
    }
}
