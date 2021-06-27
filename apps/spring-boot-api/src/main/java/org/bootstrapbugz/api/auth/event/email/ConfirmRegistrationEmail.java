package org.bootstrapbugz.api.auth.event.email;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class ConfirmRegistrationEmail implements JwtEmail {
  @Override
  public void sendEmail(
      EmailService emailService, User user, String token, String uiAppUrl, String appName) {
    try {
      File template = new ClassPathResource("templates/email/confirm-registration.html").getFile();
      String body = Files.asCharSource(template, StandardCharsets.UTF_8).read();
      String link = uiAppUrl + "/confirm-registration?token=" + token;
      body =
          body.replace("$name", user.getUsername())
              .replace("$link", link)
              .replace("$appName", appName);
      emailService.sendHtmlEmail(user.getEmail(), "Confirm Registration", body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
