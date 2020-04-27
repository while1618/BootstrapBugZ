package com.app.bootstrapbugz.service.impl;

import com.app.bootstrapbugz.service.EmailService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(body);
        mailSender.send(email);
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, String pathToAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(message, true);

        email.setTo(to);
        email.setSubject(subject);
        email.setText(body);

        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        email.addAttachment("Invoice", file);

        mailSender.send(message);
    }
}
