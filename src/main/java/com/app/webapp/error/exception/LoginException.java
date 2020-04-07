package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private String domain = "login";

    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }
}
