package org.bootstrapbugz.api.auth.jwt.service

import java.util.Optional
import org.bootstrapbugz.api.user.payload.dto.RoleDTO

interface RefreshTokenService {
  fun create(userId: Long?, roleDTOs: Set<RoleDTO>, ipAddress: String): String

  fun check(token: String)

  fun findByUserIdAndIpAddress(userId: Long?, ipAddress: String): Optional<String>

  fun delete(token: String)

  fun deleteByUserIdAndIpAddress(userId: Long?, ipAddress: String)

  fun deleteAllByUserId(userId: Long?)
}
