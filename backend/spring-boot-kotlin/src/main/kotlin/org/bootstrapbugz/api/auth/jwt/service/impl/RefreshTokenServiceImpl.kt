package org.bootstrapbugz.api.auth.jwt.service.impl

import com.auth0.jwt.JWT
import java.time.Instant
import java.util.Optional
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenStoreRepository
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.payload.dto.RoleDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RefreshTokenServiceImpl(
  private val refreshTokenStoreRepository: RefreshTokenStoreRepository,
  private val messageService: MessageService,
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.refresh-token.duration}") private val tokenDuration: Int
) : RefreshTokenService {

  companion object {
    private val PURPOSE = JwtPurpose.REFRESH_TOKEN
  }

  override fun create(userId: Long?, roleDTOs: Set<RoleDTO>, ipAddress: String): String {
    val token =
      JWT.create()
        .withIssuer(userId.toString())
        .withClaim("roles", roleDTOs.map { it.name })
        .withClaim("purpose", PURPOSE.name)
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plusSeconds(tokenDuration.toLong()))
        .sign(JwtUtil.getAlgorithm(secret))
    refreshTokenStoreRepository.save(
      RefreshTokenStore(token, userId, ipAddress, tokenDuration.toLong())
    )
    return token
  }

  override fun check(token: String) {
    verifyToken(token)
    isInRefreshTokenStore(token)
  }

  private fun verifyToken(token: String) {
    try {
      JwtUtil.verify(token, secret, PURPOSE)
    } catch (e: RuntimeException) {
      throw BadRequestException("token", e.message)
    }
  }

  private fun isInRefreshTokenStore(token: String) {
    if (!refreshTokenStoreRepository.existsById(token))
      throw BadRequestException("token", messageService.getMessage("token.invalid"))
  }

  override fun findByUserIdAndIpAddress(userId: Long?, ipAddress: String): Optional<String> {
    return refreshTokenStoreRepository
      .findByUserIdAndIpAddress(userId, ipAddress)
      .map(RefreshTokenStore::refreshToken)
  }

  override fun delete(token: String) {
    refreshTokenStoreRepository.deleteById(token)
  }

  override fun deleteByUserIdAndIpAddress(userId: Long?, ipAddress: String) {
    refreshTokenStoreRepository.findByUserIdAndIpAddress(userId, ipAddress).ifPresent {
      entity: RefreshTokenStore ->
      refreshTokenStoreRepository.delete(entity)
    }
  }

  override fun deleteAllByUserId(userId: Long?) {
    val refreshTokens = refreshTokenStoreRepository.findAllByUserId(userId)
    refreshTokenStoreRepository.deleteAll(refreshTokens)
  }
}
