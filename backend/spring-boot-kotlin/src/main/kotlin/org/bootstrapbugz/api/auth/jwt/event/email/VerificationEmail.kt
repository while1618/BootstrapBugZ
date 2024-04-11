package org.bootstrapbugz.api.auth.jwt.event.email

import org.bootstrapbugz.api.shared.email.service.EmailService
import org.bootstrapbugz.api.user.model.User
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.nio.charset.StandardCharsets

class VerificationEmail : JwtEmail {

    companion object {
        private val log = LoggerFactory.getLogger(VerificationEmail::class.java)
    }

    override fun sendEmail(emailService: EmailService, environment: Environment, user: User, token: String) {
        try {
            val template = ClassPathResource("templates/email/verify-email.html").file
            val link = "${environment.getProperty("ui.app.url")}/auth/verify-email?token=$token"
            val body = template.readText(StandardCharsets.UTF_8)
                .replace("\$name", user.username)
                .replace("\$link", link)
                .replace(
                    "\$appName",
                    environment.getProperty("app.name") ?: throw IllegalStateException("app.name property must be set")
                )
            emailService.sendHtmlEmail(user.email, "Verify email", body)
        } catch (e: IOException) {
            log.error(e.message, e)
        }
    }
}
