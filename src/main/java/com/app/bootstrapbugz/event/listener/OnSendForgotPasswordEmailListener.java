package com.app.bootstrapbugz.event.listener;

import com.app.bootstrapbugz.event.OnSendForgotPasswordEmail;
import com.app.bootstrapbugz.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class OnSendForgotPasswordEmailListener implements ApplicationListener<OnSendForgotPasswordEmail> {
    private final EmailService emailService;
    private final String port;
    private final String hostname;

    public OnSendForgotPasswordEmailListener(EmailService emailService, Environment environment) throws UnknownHostException {
        this.emailService = emailService;
        hostname = InetAddress.getLocalHost().getHostName();
        port = environment.getProperty("server.port");
    }

    @Override
    public void onApplicationEvent(OnSendForgotPasswordEmail event) {
        String subject = "Forgot Password";
        String body = "Please go to this link to change your password.\n" +
                "http://" + hostname + ":" + port + "/api/auth/reset-password?token=" + event.getToken();

        emailService.sendEmail(event.getUser().getEmail(), subject, body);
    }
}
