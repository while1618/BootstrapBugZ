package org.bootstrapbugz.api.shared.email.service.impl;

import java.nio.charset.StandardCharsets;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;

  public EmailServiceImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendHtmlEmail(String to, String subject, String body) {
    try {
      final var mimeMessage = mailSender.createMimeMessage();
      final var helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      log.error(e.getMessage());
    }
  }
}
