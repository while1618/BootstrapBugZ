package org.bootstrapbugz.api.auth.util

import jakarta.servlet.http.HttpServletRequest
import org.bootstrapbugz.api.auth.security.UserPrincipal
import org.bootstrapbugz.api.user.model.Role.RoleName
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder

object AuthUtil {
  fun isSignedIn(): Boolean {
    val auth = SecurityContextHolder.getContext().authentication
    return auth.principal != "anonymousUser"
  }

  fun isAdminSignedIn(): Boolean {
    if (!isSignedIn()) return false
    val userPrincipal = findSignedInUser()
    return userPrincipal.authorities?.any { authority ->
      authority.authority == RoleName.ADMIN.name
    } ?: false
  }

  fun findSignedInUser(): UserPrincipal {
    val auth = SecurityContextHolder.getContext().authentication
    return auth.principal as UserPrincipal
  }

  fun getUserIpAddress(request: HttpServletRequest): String {
    val ipAddress = request.getHeader("x-forwarded-for")
    return if (ipAddress == null || ipAddress.isEmpty()) request.remoteAddr else ipAddress
  }

  fun getAccessTokenFromRequest(request: HttpServletRequest): String? {
    return request.getHeader(HttpHeaders.AUTHORIZATION)
  }
}
