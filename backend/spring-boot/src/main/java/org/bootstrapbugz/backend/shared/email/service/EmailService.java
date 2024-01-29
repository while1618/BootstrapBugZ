package org.bootstrapbugz.backend.shared.email.service;

public interface EmailService {
  void sendHtmlEmail(String to, String subject, String body);
}
