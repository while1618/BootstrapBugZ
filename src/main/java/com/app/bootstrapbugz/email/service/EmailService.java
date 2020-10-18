package com.app.bootstrapbugz.email.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
