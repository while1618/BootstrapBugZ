package org.bootstrapbugz.api.auth.jwt.service

import org.bootstrapbugz.api.user.model.User

interface ResetPasswordTokenService {
  fun create(userId: Long?): String

  fun check(token: String)

  fun sendToEmail(user: User, token: String)
}
