package org.bootstrapbugz.backend.auth.jwt.event.listener;

import org.bootstrapbugz.backend.auth.jwt.event.OnSendJwtEmail;
import org.bootstrapbugz.backend.auth.jwt.event.email.JwtEmailSupplier;
import org.bootstrapbugz.backend.shared.email.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OnSendJwtEmailListener implements ApplicationListener<OnSendJwtEmail> {
  private final EmailService emailService;
  private final Environment environment;

  public OnSendJwtEmailListener(EmailService emailService, Environment environment) {
    this.emailService = emailService;
    this.environment = environment;
  }

  @Override
  public void onApplicationEvent(OnSendJwtEmail event) {
    new JwtEmailSupplier()
        .supplyEmail(event.getPurpose())
        .sendEmail(emailService, environment, event.getUser(), event.getToken());
  }
}
