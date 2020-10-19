package com.app.bootstrapbugz.jwt.event.listener;

import com.app.bootstrapbugz.jwt.util.JwtPurpose;
import com.app.bootstrapbugz.jwt.event.OnSendJwtEmail;
import com.app.bootstrapbugz.email.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class OnSendJwtEmailListener implements ApplicationListener<OnSendJwtEmail> {
    private final EmailService emailService;
    private final String port;
    private final String hostname;

    public OnSendJwtEmailListener(EmailService emailService, Environment environment) throws UnknownHostException {
        this.emailService = emailService;
        hostname = InetAddress.getLocalHost().getHostName();
        port = environment.getProperty("server.port");
    }

    @Override
    public void onApplicationEvent(OnSendJwtEmail event) {
        if (event.getPurpose().equals(JwtPurpose.CONFIRM_REGISTRATION))
            sendConfirmRegistrationEmail(event.getToken(), event.getUser().getEmail());

        if (event.getPurpose().equals(JwtPurpose.FORGOT_PASSWORD))
            sendForgotPasswordEmail(event.getToken(), event.getUser().getEmail());
    }

    private void sendConfirmRegistrationEmail(String token, String email) {
        String subject = "Activate Account";
        String body = "Please activate your account by clicking on link.\n" +
                "http://" + hostname + ":" + port + "/api/auth/confirm-registration?token=" + token;

        emailService.sendEmail(email, subject, body);
    }

    private void sendForgotPasswordEmail(String token, String email) {
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://" + hostname + ":" + port + "/api/auth/reset-password?token=" + token;

        emailService.sendEmail(email, subject, body);
    }
}
