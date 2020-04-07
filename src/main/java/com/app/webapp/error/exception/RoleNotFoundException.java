package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private String domain = "role.notFound";

    public RoleNotFoundException() {

    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
