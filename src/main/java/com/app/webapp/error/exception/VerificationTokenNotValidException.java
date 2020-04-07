package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class VerificationTokenNotValidException extends RuntimeException {
    private String domain = "verificationToken.notValid";

    public VerificationTokenNotValidException() {

    }

    public VerificationTokenNotValidException(String message) {
        super(message);
    }
}
