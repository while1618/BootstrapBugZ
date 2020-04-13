package com.app.webapp.error.exception;

import com.app.webapp.error.ErrorDomains;
import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private String domain = ErrorDomains.LOGIN;

    public LoginException(String message) {
        super(message);
    }
}
