package org.bootstrapbugz.api.auth.jwt.service.impl

import com.auth0.jwt.JWT
import org.bootstrapbugz.api.auth.jwt.redis.model.AccessTokenBlacklist
import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist
import org.bootstrapbugz.api.auth.jwt.redis.repository.AccessTokenBlacklistRepository
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.getAlgorithm
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.getIssuedAt
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.getUserId
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.verify
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.payload.dto.RoleDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AccessTokenServiceImpl(
    private val accessTokenBlacklistRepository: AccessTokenBlacklistRepository,
    private val userBlacklistRepository: UserBlacklistRepository,
    private val messageService: MessageService,
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token.duration}") private val tokenDuration: Int
) : AccessTokenService {

    companion object {
        private val PURPOSE = JwtPurpose.ACCESS_TOKEN
    }

    override fun create(userId: Long?, roleDTOs: Set<RoleDTO>): String {
        return JWT.create()
            .withIssuer(userId.toString())
            .withClaim("roles",  roleDTOs.map { it.name }.toList())
            .withClaim("purpose", PURPOSE.name)
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusSeconds(tokenDuration.toLong()))
            .sign(getAlgorithm(secret))
    }

    override fun check(token: String) {
        verifyToken(token)
        isInAccessTokenBlacklist(token)
        isInUserBlacklist(token)
    }

    private fun verifyToken(token: String) {
        try {
            verify(token, secret, PURPOSE)
        } catch (e: RuntimeException) {
            throw UnauthorizedException(e.message)
        }
    }

    private fun isInAccessTokenBlacklist(token: String) {
        if (accessTokenBlacklistRepository.existsById(token))
            throw UnauthorizedException(messageService.getMessage("token.invalid"))
    }

    private fun isInUserBlacklist(token: String) {
        val userId = getUserId(token)
        val issuedAt = getIssuedAt(token)
        userBlacklistRepository.findById(userId).let {
            if (issuedAt.isBefore(it.get().updatedAt) || issuedAt == it.get().updatedAt)
                throw UnauthorizedException(messageService.getMessage("token.invalid"))
        }
    }

    override fun invalidate(token: String) {
        accessTokenBlacklistRepository.save(AccessTokenBlacklist(token, tokenDuration.toLong()))
    }

    override fun invalidateAllByUserId(userId: Long?) {
        val userBlacklist = UserBlacklist(userId, tokenDuration.toLong(), Instant.now().truncatedTo(ChronoUnit.SECONDS))
        userBlacklistRepository.save(userBlacklist)
    }
}
