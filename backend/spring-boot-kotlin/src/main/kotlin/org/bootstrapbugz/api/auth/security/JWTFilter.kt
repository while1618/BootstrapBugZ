package org.bootstrapbugz.api.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.util.AuthUtil
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTFilter(
  private val accessTokenService: AccessTokenService,
  private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

  companion object {
    private val log = LoggerFactory.getLogger(JWTFilter::class.java)
  }

  @Throws(IOException::class, ServletException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    chain: FilterChain
  ) {
    val accessToken = AuthUtil.getAccessTokenFromRequest(request)
    if (accessToken == null || !JwtUtil.isBearer(accessToken)) {
      chain.doFilter(request, response)
      return
    }
    try {
      SecurityContextHolder.getContext().authentication = getAuth(accessToken)
    } catch (e: RuntimeException) {
      log.error(e.message)
    } finally {
      chain.doFilter(request, response)
    }
  }

  private fun getAuth(accessToken: String): UsernamePasswordAuthenticationToken {
    val token = JwtUtil.removeBearer(accessToken)
    accessTokenService.check(token)
    val userId = JwtUtil.getUserId(token)
    val userPrincipal = userDetailsService.loadUserByUserId(userId) as UserPrincipal
    return UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
  }
}
