package com.app.webapp.error.exception;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException() {
    }

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
