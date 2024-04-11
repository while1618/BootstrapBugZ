package org.bootstrapbugz.api.auth.jwt.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import org.bootstrapbugz.api.user.payload.dto.RoleDTO

object JwtUtil {
  private const val BEARER = "Bearer "

  fun verify(token: String, secret: String, purpose: JwtPurpose) {
    JWT.require(getAlgorithm(secret)).withClaim("purpose", purpose.name).build().verify(token)
  }

  fun getAlgorithm(secret: String): Algorithm {
    return Algorithm.HMAC512(secret.toByteArray())
  }

  fun removeBearer(token: String): String {
    return token.replace(BEARER, "")
  }

  fun isBearer(token: String): Boolean {
    return token.startsWith(BEARER)
  }

  fun getUserId(token: String?): Long {
    return JWT.decode(token).issuer.toLong()
  }

  fun getRoleDTOs(token: String): Set<RoleDTO> {
    val roles = JWT.decode(token).getClaim("roles").asList(String::class.java)
    return roles.map { RoleDTO(it) }.toSet()
  }

  fun getIssuedAt(token: String): Instant {
    return JWT.decode(token).issuedAtAsInstant
  }

  enum class JwtPurpose {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    VERIFY_EMAIL_TOKEN,
    RESET_PASSWORD_TOKEN
  }
}
