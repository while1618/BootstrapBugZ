package org.bootstrapbugz.api.auth.jwt.event

import java.io.Serial
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose
import org.bootstrapbugz.api.user.model.User
import org.springframework.context.ApplicationEvent

class OnSendJwtEmail(val user: User, val token: String, val purpose: JwtPurpose) :
  ApplicationEvent(user) {
  companion object {
    @Serial private val serialVersionUID = 6234594744610595282L
  }
}
