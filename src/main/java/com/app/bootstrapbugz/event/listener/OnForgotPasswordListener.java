package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnForgotPasswordEvent;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.security.jwt.JwtProperties;
import com.app.bootstrapbugz.security.jwt.JwtUtilities;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnForgotPasswordListener implements ApplicationListener<OnForgotPasswordEvent> {
    private final JwtUtilities jwtUtilities;
    private final EmailService emailService;

    public OnForgotPasswordListener(JwtUtilities jwtUtilities, EmailService emailService) {
        this.jwtUtilities = jwtUtilities;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnForgotPasswordEvent event) {
        User user = event.getUser();
        String token = jwtUtilities.createToken(user, JwtProperties.FORGOT_PASSWORD);
        sendEmail(user.getEmail(), token);
    }

    private void sendEmail(String to, String token) {
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://localhost:12345/api/auth/reset-password?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
