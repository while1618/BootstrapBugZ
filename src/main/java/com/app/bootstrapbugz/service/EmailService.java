package com.app.bootstrapbugz.service;

import javax.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailWithAttachment(String to, String subject, String body, String pathToAttachment) throws MessagingException;
}
