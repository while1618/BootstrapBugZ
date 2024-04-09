package org.bootstrapbugz.api.auth.jwt.event.email

import org.bootstrapbugz.api.shared.email.service.EmailService
import org.bootstrapbugz.api.user.model.User
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class ResetPasswordEmail : JwtEmail {
    companion object {
        private val log = LoggerFactory.getLogger(ResetPasswordEmail::class.java)
    }

    override fun sendEmail(emailService: EmailService, environment: Environment, user: User, token: String) {
        try {
            val templateFile = ClassPathResource("templates/email/reset-password.html").file
            val link = "${environment.getProperty("ui.app.url")}/auth/reset-password?token=$token"
            val body = templateFile.readText(StandardCharsets.UTF_8)
                .replace("\$name", user.username)
                .replace("\$link", link)
                .replace("\$appName", Objects.requireNonNull(environment.getProperty("app.name")))
            emailService.sendHtmlEmail(user.email, "Reset password", body)
        } catch (e: IOException) {
            log.error(e.message ?: "An error occurred while sending reset password email", e)
        }
    }
}
