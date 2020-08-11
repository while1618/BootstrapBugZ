package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnSendConfirmationEmail;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendConfirmationEmailListener implements ApplicationListener<OnSendConfirmationEmail> {
    private final EmailService emailService;

    public OnSendConfirmationEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnSendConfirmationEmail event) {
        String subject = "Activate Account";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + event.getToken();

        emailService.sendEmail(event.getUser().getEmail(), subject, body);
    }
}
