package org.bootstrapbugz.api.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JWTFilter extends OncePerRequestFilter {
  private final AccessTokenService accessTokenService;
  private final ExtendedUserDetailsService userDetailsService;

  public JWTFilter(
      AccessTokenService accessTokenService, ExtendedUserDetailsService userDetailsService) {
    this.accessTokenService = accessTokenService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain chain)
      throws IOException, ServletException {
    final var accessToken = AuthUtil.getAccessTokenFromRequest(request);
    if (accessToken == null || !JwtUtil.isBearer(accessToken)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      SecurityContextHolder.getContext().setAuthentication(getAuth(accessToken));
    } catch (RuntimeException e) {
      log.error(e.getMessage());
    } finally {
      chain.doFilter(request, response);
    }
  }

  private UsernamePasswordAuthenticationToken getAuth(String accessToken) {
    final var token = JwtUtil.removeBearer(accessToken);
    accessTokenService.check(token);
    final var userId = JwtUtil.getUserId(token);
    final var userPrincipal = (UserPrincipal) userDetailsService.loadUserByUserId(userId);
    return new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
  }
}
