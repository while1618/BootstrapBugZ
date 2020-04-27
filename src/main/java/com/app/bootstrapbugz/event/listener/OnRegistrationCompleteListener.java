package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnRegistrationCompleteEvent;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.security.jwt.JwtProperties;
import com.app.bootstrapbugz.security.jwt.JwtUtilities;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnRegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final JwtUtilities jwtUtilities;
    private final EmailService emailService;

    public OnRegistrationCompleteListener(JwtUtilities jwtUtilities, EmailService emailService) {
        this.jwtUtilities = jwtUtilities;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
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
