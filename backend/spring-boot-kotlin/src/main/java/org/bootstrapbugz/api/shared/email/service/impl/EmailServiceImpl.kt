package org.bootstrapbugz.api.shared.email.service.impl

import jakarta.mail.MessagingException
import org.bootstrapbugz.api.shared.email.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class EmailServiceImpl(private val mailSender: JavaMailSender) : EmailService {

    companion object {
        private val log = LoggerFactory.getLogger(EmailServiceImpl::class.java)
    }

    override fun sendHtmlEmail(to: String, subject: String, body: String) {
        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name())
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(body, true)
            mailSender.send(mimeMessage)
        } catch (e: MessagingException) {
            log.error(e.message ?: "Error sending email")
        }
    }
}