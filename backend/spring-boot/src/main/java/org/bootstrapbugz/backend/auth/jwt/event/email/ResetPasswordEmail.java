package org.bootstrapbugz.backend.auth.jwt.event.email;

import com.google.common.io.Files;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.shared.email.service.EmailService;
import org.bootstrapbugz.backend.user.model.User;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class ResetPasswordEmail implements JwtEmail {
  @Override
  public void sendEmail(EmailService emailService, Environment environment, User user, String token)
      throws IOException, MessagingException {
    final var template = new ClassPathResource("templates/email/reset-password.html").getFile();
    final var link = environment.getProperty("ui.app.url") + "/auth/reset-password?token=" + token;
    final var body =
        Files.asCharSource(template, StandardCharsets.UTF_8)
            .read()
            .replace("$name", user.getUsername())
            .replace("$link", link)
            .replace("$appName", Objects.requireNonNull(environment.getProperty("app.name")));
    emailService.sendHtmlEmail(user.getEmail(), "Reset password", body);
  }
}
