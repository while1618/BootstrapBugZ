package com.app.webapp.event.listener;

import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.VerificationTokenRepository;
import com.app.webapp.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OnRegistrationCompleteListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public OnRegistrationCompleteListener(VerificationTokenRepository verificationTokenRepository, EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = generateToken(user);
        sendEmail(user, token);
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokenRepository.save(new VerificationToken(user, token));
        return token;
    }

    private void sendEmail(User user, String token) {
        String to = user.getEmail();
        String subject = "Registration Confirmation";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
