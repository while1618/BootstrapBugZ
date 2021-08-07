package org.bootstrapbugz.api.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bootstrapbugz.api.auth.security.user.details.CustomUserDetailsService;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      CustomUserDetailsService userDetailsService) {
    super(authenticationManager);
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final String token = AuthUtil.getAuthTokenFromRequest(request);
    if (token == null || !token.startsWith(JwtUtil.TOKEN_TYPE)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      final var authToken = getAuthenticationToken(JwtUtil.removeTokenTypeFromToken(token));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (RuntimeException e) {
      log.error(e.getMessage());
    } finally {
      chain.doFilter(request, response);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
    final var userPrincipal = (UserPrincipal) userDetailsService.loadUserByUserId(JwtUtil.getUserId(token));
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);

    return new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
  }
}
