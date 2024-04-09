package org.bootstrapbugz.api.auth.jwt.event.listener

import org.bootstrapbugz.api.auth.jwt.event.OnSendJwtEmail
import org.bootstrapbugz.api.auth.jwt.event.email.JwtEmailSupplier
import org.bootstrapbugz.api.shared.email.service.EmailService
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class OnSendJwtEmailListener(
    private val emailService: EmailService,
    private val environment: Environment
) : ApplicationListener<OnSendJwtEmail> {

    override fun onApplicationEvent(event: OnSendJwtEmail) {
        JwtEmailSupplier
            .supplyEmail(event.purpose)
            .sendEmail(emailService, environment, event.user, event.token)
    }
}