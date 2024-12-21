package org.bootstrapbugz.api.auth.jwt.event.email;

import com.google.common.io.CharStreams;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

public class ResetPasswordEmail implements JwtEmail {
  @Override
  public void sendEmail(EmailService emailService, Environment environment, User user, String token)
      throws IOException, MessagingException {
    final var template =
        new ClassPathResource("templates/email/reset-password.html").getInputStream();
    final var link = environment.getProperty("ui.app.url") + "/auth/reset-password?token=" + token;
    final var body =
        CharStreams.toString(new InputStreamReader(template, StandardCharsets.UTF_8))
            .replace("${name}", user.getUsername())
            .replace("${link}", link)
            .replace("${appName}", Objects.requireNonNull(environment.getProperty("app.name")));
    emailService.sendHtmlEmail(user.getEmail(), "Reset password", body);
  }
}
