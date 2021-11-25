package org.bootstrapbugz.api.shared.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.bootstrapbugz.api.shared.email.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
  @Mock private JavaMailSender mailSender;

  @InjectMocks private EmailServiceImpl emailService;

  @Test
  void itShouldSendHtmlEmail() {
    String to = "user";
    String subject = "subject";
    String body =
        "<html><head><title>Title</title></head><body><p>This is an html email.</p></body></html>";
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    emailService.sendHtmlEmail(to, subject, body);
    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }
}
