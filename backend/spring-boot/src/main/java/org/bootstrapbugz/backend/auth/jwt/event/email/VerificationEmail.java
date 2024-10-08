package org.bootstrapbugz.backend.auth.jwt.event.email;

import com.google.common.io.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.shared.email.service.EmailService;
import org.bootstrapbugz.backend.user.model.User;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class VerificationEmail implements JwtEmail {
  @Override
  public void sendEmail(
      EmailService emailService, Environment environment, User user, String token) {
    try {
      final var template = new ClassPathResource("templates/email/verify-email.html").getFile();
      final var link = environment.getProperty("ui.app.url") + "/auth/verify-email?token=" + token;
      final var body =
          Files.asCharSource(template, StandardCharsets.UTF_8)
              .read()
              .replace("$name", user.getUsername())
              .replace("$link", link)
              .replace("$appName", Objects.requireNonNull(environment.getProperty("app.name")));
      emailService.sendHtmlEmail(user.getEmail(), "Verify email", body);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
