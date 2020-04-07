package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class UserNotAuthorizedException extends RuntimeException {
    private String domain = "user.notAuthorized";

    public UserNotAuthorizedException() {

    }

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
