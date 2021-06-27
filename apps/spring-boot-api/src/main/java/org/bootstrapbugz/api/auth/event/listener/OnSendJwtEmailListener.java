package org.bootstrapbugz.api.auth.event.listener;

import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.event.email.JwtEmailSupplier;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendJwtEmailListener implements ApplicationListener<OnSendJwtEmail> {
  @Value("${ui.app.url}")
  private String uiAppUrl;

  @Value("${app.name}")
  private String appName;

  private final EmailService emailService;

  public OnSendJwtEmailListener(EmailService emailService) {
    this.emailService = emailService;
  }

  @Override
  public void onApplicationEvent(OnSendJwtEmail event) {
    new JwtEmailSupplier()
        .supplyEmail(event.getPurpose())
        .sendEmail(emailService, event.getUser(), event.getToken(), uiAppUrl, appName);
  }
}
