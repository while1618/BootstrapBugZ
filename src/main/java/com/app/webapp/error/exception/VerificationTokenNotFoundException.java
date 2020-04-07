package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class VerificationTokenNotFoundException extends RuntimeException {
    private String domain = "verificationToken.notFound";

    public VerificationTokenNotFoundException() {

    }

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
