package com.app.webapp.event.listener;

import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.VerificationTokenRepository;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OnResendVerificationEmailListener implements ApplicationListener<OnResendVerificationEmailEvent> {
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public OnResendVerificationEmailListener(VerificationTokenRepository verificationTokenRepository, EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnResendVerificationEmailEvent event) {
        VerificationToken verificationToken = event.getVerificationToken();
        String token = updateToken(verificationToken);
        sendEmail(verificationToken.getUser().getEmail(), token);
    }

    private String updateToken(VerificationToken verificationToken) {
        String token = UUID.randomUUID().toString();
        verificationToken.updateToken(token);
        verificationToken.extendExpirationDate();
        verificationToken.increaseNumberOfSent();
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    private void sendEmail(String to, String token) {
        String subject = "Resend Registration Confirmation";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
