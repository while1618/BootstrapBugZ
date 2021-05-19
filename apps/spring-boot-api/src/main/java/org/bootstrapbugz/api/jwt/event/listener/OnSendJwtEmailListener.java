package org.bootstrapbugz.api.jwt.event.listener;

import org.bootstrapbugz.api.jwt.event.OnSendJwtEmail;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OnSendJwtEmailListener implements ApplicationListener<OnSendJwtEmail> {
  @Value("${ui.app.url}")
  private String uiAppUrl;

  @Value("${app.name}")
  private String appName;

  private final EmailService emailService;

  public OnSendJwtEmailListener(EmailService emailService) {
    this.emailService = emailService;
  }

  @Override
  public void onApplicationEvent(OnSendJwtEmail event) {
    switch (event.getPurpose()) {
      case CONFIRM_REGISTRATION -> sendEmail(event, "Confirm Registration", "confirm-registration");
      case FORGOT_PASSWORD -> sendEmail(event, "Reset Password", "reset-password");
      default -> log.error("Unknown JWT purpose");
    }
  }

  private void sendEmail(OnSendJwtEmail event, String subject, String path) {
    try {
      File template = new ClassPathResource("templates/email/" + path + ".html").getFile();
      String body = Files.asCharSource(template, StandardCharsets.UTF_8).read();
      String link = uiAppUrl + "/" + path + "?token=" + event.getToken();
      body =
          body.replace("$name", event.getUser().getUsername())
              .replace("$link", link)
              .replace("$appName", appName);
      emailService.sendHtmlEmail(event.getUser().getEmail(), subject, body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
