package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistException extends RuntimeException {
    private String domain = "user.exists";

    public UserAlreadyExistException() {

    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
