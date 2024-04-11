package org.bootstrapbugz.api.auth.jwt.service.impl

import com.auth0.jwt.JWT
import java.time.Instant
import org.bootstrapbugz.api.auth.jwt.event.OnSendJwtEmail
import org.bootstrapbugz.api.auth.jwt.service.VerificationTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.user.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class VerificationTokenServiceImpl(
  private val eventPublisher: ApplicationEventPublisher,
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.verify-email-token.duration}") private val tokenDuration: Int
) : VerificationTokenService {

  companion object {
    private val PURPOSE = JwtPurpose.VERIFY_EMAIL_TOKEN
  }

  override fun create(userId: Long?): String {
    return JWT.create()
      .withIssuer(userId.toString())
      .withClaim("purpose", PURPOSE.name)
      .withIssuedAt(Instant.now())
      .withExpiresAt(Instant.now().plusSeconds(tokenDuration.toLong()))
      .sign(JwtUtil.getAlgorithm(secret))
  }

  override fun check(token: String) {
    try {
      JwtUtil.verify(token, secret, PURPOSE)
    } catch (e: RuntimeException) {
      throw BadRequestException("token", e.message)
    }
  }

  override fun sendToEmail(user: User, token: String) {
    eventPublisher.publishEvent(OnSendJwtEmail(user, token, PURPOSE))
  }
}
