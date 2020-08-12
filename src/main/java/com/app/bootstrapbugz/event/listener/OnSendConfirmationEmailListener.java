package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnSendConfirmationEmail;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class OnSendConfirmationEmailListener implements ApplicationListener<OnSendConfirmationEmail> {
    private final EmailService emailService;
    private final String hostname;
    private final String port;

    public OnSendConfirmationEmailListener(EmailService emailService, Environment environment) throws UnknownHostException {
        this.emailService = emailService;
        hostname = InetAddress.getLocalHost().getHostName();
        port = environment.getProperty("server.port");
    }

    @Override
    public void onApplicationEvent(OnSendConfirmationEmail event) {
        String subject = "Activate Account";
        String body = "Please activate your account by clicking on link.\n" +
                "http://" + hostname + ":" + port + "/api/auth/confirm-registration?token=" + event.getToken();

        emailService.sendEmail(event.getUser().getEmail(), subject, body);
    }
}
