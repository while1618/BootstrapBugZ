package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.constant.EmailPurpose;
import com.app.bootstrapbugz.event.OnSendEmailToUser;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.constant.JwtProperties;
import com.app.bootstrapbugz.security.jwt.JwtUtilities;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnSendEmailToUserListener implements ApplicationListener<OnSendEmailToUser> {
    private final JwtUtilities jwtUtilities;
    private final EmailService emailService;

    public OnSendEmailToUserListener(JwtUtilities jwtUtilities, EmailService emailService) {
        this.jwtUtilities = jwtUtilities;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnSendEmailToUser event) {
        User user = event.getUser();
        String purpose = event.getPurpose();

        if (purpose.equals(EmailPurpose.CONFIRM_REGISTRATION)){
            String token = jwtUtilities.createToken(user, JwtProperties.CONFIRM_REGISTRATION);
            sendConfirmRegistrationEmail(user.getEmail(), token);
        }
        if (purpose.equals(EmailPurpose.FORGOT_PASSWORD)) {
            String token = jwtUtilities.createToken(user, JwtProperties.FORGOT_PASSWORD);
            sendForgotPasswordEmail(user.getEmail(), token);
        }
    }

    private void sendConfirmRegistrationEmail(String to, String token) {
        String subject = "Activate Account";
        String body = "Please activate your account by clicking on link.\n" +
                "http://localhost:12345/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(to, subject, body);
    }

    private void sendForgotPasswordEmail(String to, String token) {
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://localhost:12345/api/auth/reset-password?token=" + token;

        emailService.sendEmail(to, subject, body);
    }
}
