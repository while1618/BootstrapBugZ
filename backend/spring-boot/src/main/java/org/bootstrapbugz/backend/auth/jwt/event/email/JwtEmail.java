package org.bootstrapbugz.backend.auth.jwt.event.email;

import jakarta.mail.MessagingException;
import java.io.IOException;
import org.bootstrapbugz.backend.shared.email.service.EmailService;
import org.bootstrapbugz.backend.user.model.User;
import org.springframework.core.env.Environment;

public interface JwtEmail {
  void sendEmail(EmailService emailService, Environment environment, User user, String token)
      throws IOException, MessagingException;
}
