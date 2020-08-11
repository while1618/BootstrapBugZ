package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnSendForgotPasswordEmail;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendForgotPasswordEmailListener implements ApplicationListener<OnSendForgotPasswordEmail> {
    private final EmailService emailService;

    public OnSendForgotPasswordEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnSendForgotPasswordEmail event) {
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://localhost:12345/api/auth/reset-password?token=" + event.getToken();

        emailService.sendEmail(event.getUser().getEmail(), subject, body);
    }
}
