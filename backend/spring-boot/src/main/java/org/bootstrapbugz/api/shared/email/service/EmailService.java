package org.bootstrapbugz.api.shared.email.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  void sendHtmlEmail(String to, String subject, String body) throws MessagingException;
}
