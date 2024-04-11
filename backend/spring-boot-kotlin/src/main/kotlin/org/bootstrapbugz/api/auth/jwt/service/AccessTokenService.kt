package org.bootstrapbugz.api.auth.jwt.service

import org.bootstrapbugz.api.user.payload.dto.RoleDTO

interface AccessTokenService {
  fun create(userId: Long?, roleDTOs: Set<RoleDTO>): String

  fun check(token: String)

  fun invalidate(token: String)

  fun invalidateAllByUserId(userId: Long?)
}
