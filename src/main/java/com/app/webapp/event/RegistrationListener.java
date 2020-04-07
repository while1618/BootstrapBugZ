package com.app.webapp.event;

import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.service.VerificationTokenService;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public RegistrationListener(VerificationTokenService verificationTokenService, EmailService emailService) {
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.save(new VerificationToken(user, token));
        String to = user.getEmail();
        String subject = "Registration Confirmation";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
