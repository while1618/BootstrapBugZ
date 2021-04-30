package com.app.bootstrapbugz.shared.email.service;

public interface EmailService {
  void sendHtmlEmail(String to, String subject, String body);
}
