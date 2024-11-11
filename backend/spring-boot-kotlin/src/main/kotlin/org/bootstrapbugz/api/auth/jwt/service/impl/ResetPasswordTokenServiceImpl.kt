package org.bootstrapbugz.api.auth.jwt.service.impl

import com.auth0.jwt.JWT
import java.time.Instant
import org.bootstrapbugz.api.auth.jwt.event.OnSendJwtEmail
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository
import org.bootstrapbugz.api.auth.jwt.service.ResetPasswordTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class ResetPasswordTokenServiceImpl(
  private val userBlacklistRepository: UserBlacklistRepository,
  private val messageService: MessageService,
  private val eventPublisher: ApplicationEventPublisher,
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.reset-password-token.duration}") private val tokenDuration: Int
) : ResetPasswordTokenService {

  companion object {
    private val PURPOSE = JwtPurpose.RESET_PASSWORD_TOKEN
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
    verifyToken(token)
    isInUserBlacklist(token)
  }

  private fun verifyToken(token: String) {
    try {
      JwtUtil.verify(token, secret, PURPOSE)
    } catch (e: RuntimeException) {
      throw BadRequestException("token", e.message)
    }
  }

  private fun isInUserBlacklist(token: String) {
    val userId = JwtUtil.getUserId(token)
    val issuedAt = JwtUtil.getIssuedAt(token)

    userBlacklistRepository.findById(userId).ifPresent {
      if (issuedAt.isBefore(it.updatedAt)) {
        throw BadRequestException("token", messageService.getMessage("token.invalid"))
      }
    }
  }

  override fun sendToEmail(user: User, token: String) {
    eventPublisher.publishEvent(OnSendJwtEmail(user, token, PURPOSE))
  }
}
