package org.bugzkit.api.auth.jwt.event.email;

import jakarta.mail.MessagingException;
import java.io.IOException;
import org.bugzkit.api.shared.email.service.EmailService;
import org.bugzkit.api.user.model.User;
import org.springframework.core.env.Environment;

public interface JwtEmail {
  void sendEmail(EmailService emailService, Environment environment, User user, String token)
      throws IOException, MessagingException;
}
