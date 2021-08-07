package org.bootstrapbugz.api.auth.event.email;

import com.google.common.io.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class ResetPasswordEmail implements JwtEmail {
  @Override
  public void sendEmail(
      EmailService emailService, Environment environment, User user, String token) {
    try {
      final var template = new ClassPathResource("templates/email/reset-password.html").getFile();
      final String link = environment.getProperty("ui.app.url") + "/reset-password?token=" + token;
      final String body =
          Files.asCharSource(template, StandardCharsets.UTF_8)
              .read()
              .replace("$name", user.getUsername())
              .replace("$link", link)
              .replace("$appName", Objects.requireNonNull(environment.getProperty("app.name")));
      emailService.sendHtmlEmail(user.getEmail(), "Reset Password", body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
