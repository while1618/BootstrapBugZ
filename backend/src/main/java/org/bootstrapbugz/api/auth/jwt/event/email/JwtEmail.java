package org.bootstrapbugz.api.auth.jwt.event.email;

import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.core.env.Environment;

public interface JwtEmail {
  void sendEmail(EmailService emailService, Environment environment, User user, String token);
}
