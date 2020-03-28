package com.app.webapp.service.utils;

import javax.mail.MessagingException;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailWithAttachment(String to, String subject, String body, String pathToAttachment) throws MessagingException;
}
