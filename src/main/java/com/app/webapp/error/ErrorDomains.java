package com.app.webapp.error;

import lombok.Getter;

@Getter
public enum ErrorDomains {
    GLOBAL ("global"),
    USER ("user"),
    AUTH ("auth"),
    ROLE ("role"),
    LOGIN ("login"),
    VERIFICATION_TOKEN ("verificationToken");

    private final String name;

    ErrorDomains(String name) {
        this.name = name;
    }
}
