package com.app.webapp.security;

public class JwtProperties {
    public static final int EXPIRATION_TIME = 3600000;  //1h
    public static final String HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String ACCESSING_RESOURCES = "accessingResources";
    public static final String CONFIRM_REGISTRATION = "confirmRegistration";
    public static final String FORGOT_PASSWORD = "forgotPassword";
}
