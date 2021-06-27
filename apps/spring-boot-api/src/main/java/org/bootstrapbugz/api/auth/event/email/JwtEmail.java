package org.bootstrapbugz.api.auth.event.email;

import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.user.model.User;

public interface JwtEmail {
  void sendEmail(
      EmailService emailService, User user, String token, String uiAppUrl, String appName);
}
