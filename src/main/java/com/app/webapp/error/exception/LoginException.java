package com.app.webapp.error.exception;

import com.app.webapp.error.ErrorDomains;
import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private ErrorDomains domain = ErrorDomains.LOGIN;

    public LoginException(String message) {
        super(message);
    }
}
