package org.bootstrapbugz.backend.shared.email.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  void sendHtmlEmail(String to, String subject, String body) throws MessagingException;
}
