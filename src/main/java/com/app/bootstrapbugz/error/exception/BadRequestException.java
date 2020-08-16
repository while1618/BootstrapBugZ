package com.app.bootstrapbugz.error.exception;

import com.app.bootstrapbugz.constant.ErrorDomain;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorDomain domain;

    public BadRequestException(String message, ErrorDomain domain) {
        super(message);
        this.domain = domain;
    }
}
