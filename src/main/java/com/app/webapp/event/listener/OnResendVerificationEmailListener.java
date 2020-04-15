package com.app.webapp.event.listener;

import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.model.AuthToken;
import com.app.webapp.repository.AuthTokenRepository;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OnResendVerificationEmailListener implements ApplicationListener<OnResendVerificationEmailEvent> {
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;

    public OnResendVerificationEmailListener(AuthTokenRepository authTokenRepository, EmailService emailService) {
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnResendVerificationEmailEvent event) {
        AuthToken authToken = event.getAuthToken();
        String token = updateToken(authToken);
        sendEmail(authToken.getUser().getEmail(), token);
    }

    private String updateToken(AuthToken authToken) {
        String token = UUID.randomUUID().toString();
        authToken.updateToken(token);
        authToken.extendExpirationDate();
        authTokenRepository.save(authToken);
        return token;
    }

    private void sendEmail(String to, String token) {
        String subject = "Resend Registration Confirmation";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
