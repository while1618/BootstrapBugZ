package com.app.bootstrapbugz.email.service;

public interface EmailService {
  void sendHtmlEmail(String to, String subject, String body);
}
