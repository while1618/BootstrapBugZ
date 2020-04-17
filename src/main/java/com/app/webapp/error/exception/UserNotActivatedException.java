package com.app.webapp.error.exception;

import com.app.webapp.error.ErrorDomains;
import lombok.Getter;

@Getter
public class UserNotActivatedException extends RuntimeException {
    private final String domain = ErrorDomains.USER;

    public UserNotActivatedException(String message) {
        super(message);
    }
}
