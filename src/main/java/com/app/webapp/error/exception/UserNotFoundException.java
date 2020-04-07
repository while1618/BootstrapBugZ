package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private String domain = "user.notFound";

    public UserNotFoundException() {

    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
