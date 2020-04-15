package com.app.webapp.event.listener;

import com.app.webapp.event.OnForgotPasswordEvent;
import com.app.webapp.model.AuthToken;
import com.app.webapp.model.AuthTokenProperties;
import com.app.webapp.model.User;
import com.app.webapp.repository.AuthTokenRepository;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OnForgotPasswordListener implements ApplicationListener<OnForgotPasswordEvent> {
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;

    public OnForgotPasswordListener(AuthTokenRepository authTokenRepository, EmailService emailService) {
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnForgotPasswordEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        authTokenRepository.save(new AuthToken(user, token, AuthTokenProperties.FORGOT_PASSWORD));
        sendEmail(user, token);
    }

    private void sendEmail(User user, String token) {
        String to = user.getEmail();
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://localhost:12345/api/auth/reset-password?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
