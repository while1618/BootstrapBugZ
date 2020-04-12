package com.app.webapp.error.exception;

import com.app.webapp.error.ErrorDomains;
import lombok.Getter;

@Getter
public class VerificationTokenNotValidException extends RuntimeException {
    private ErrorDomains domain = ErrorDomains.VERIFICATION_TOKEN;

    public VerificationTokenNotValidException(String message) {
        super(message);
    }
}
