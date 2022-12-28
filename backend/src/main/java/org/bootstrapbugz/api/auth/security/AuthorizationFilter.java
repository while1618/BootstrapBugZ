package org.bootstrapbugz.api.auth.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.security.user.details.CustomUserDetailsService;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {
  private final AccessTokenService accessTokenService;
  private final CustomUserDetailsService userDetailsService;

  public AuthorizationFilter(
      AuthenticationManager authenticationManager,
      AccessTokenService accessTokenService,
      CustomUserDetailsService userDetailsService) {
    super(authenticationManager);
    this.accessTokenService = accessTokenService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final var accessToken = AuthUtil.getAccessTokenFromRequest(request);
    if (accessToken == null || !JwtUtil.isBearer(accessToken)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      final var authToken = getAuthenticationToken(JwtUtil.removeBearer(accessToken));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (RuntimeException e) {
      log.error(e.getMessage());
    } finally {
      chain.doFilter(request, response);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(String accessToken) {
    accessTokenService.check(accessToken);
    final var userPrincipal =
        (UserPrincipal) userDetailsService.loadUserByUserId(JwtUtil.getUserId(accessToken));
    return new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
  }
}
