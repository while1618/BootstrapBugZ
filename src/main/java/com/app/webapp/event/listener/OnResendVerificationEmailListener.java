package com.app.webapp.event.listener;

import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.model.User;
import com.app.webapp.security.JwtProperties;
import com.app.webapp.security.JwtUtilities;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnResendVerificationEmailListener implements ApplicationListener<OnResendVerificationEmailEvent> {
    private final JwtUtilities jwtUtilities;
    private final EmailService emailService;

    public OnResendVerificationEmailListener(JwtUtilities jwtUtilities, EmailService emailService) {
        this.jwtUtilities = jwtUtilities;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnResendVerificationEmailEvent event) {
        User user = event.getUser();
        String token = jwtUtilities.createToken(user, JwtProperties.CONFIRM_REGISTRATION);
        sendEmail(user.getEmail(), token);
    }

    private void sendEmail(String to, String token) {
        String subject = "Activate Account";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
