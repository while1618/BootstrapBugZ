package com.app.bootstrapbugz.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
