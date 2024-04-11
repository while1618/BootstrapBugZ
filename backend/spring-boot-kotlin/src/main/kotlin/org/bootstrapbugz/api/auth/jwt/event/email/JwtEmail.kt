package org.bootstrapbugz.api.auth.jwt.event.email

import org.bootstrapbugz.api.shared.email.service.EmailService
import org.bootstrapbugz.api.user.model.User
import org.springframework.core.env.Environment

interface JwtEmail {
  fun sendEmail(emailService: EmailService, environment: Environment, user: User, token: String)
}
