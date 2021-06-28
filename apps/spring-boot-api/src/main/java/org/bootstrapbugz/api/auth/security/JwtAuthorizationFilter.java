package org.bootstrapbugz.api.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
    super(authenticationManager);
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = AuthUtil.getAuthTokenFromRequest(request);
    if (token == null || !token.startsWith(JwtUtil.TOKEN_TYPE)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      var authToken = getAuthenticationToken(JwtUtil.removeTokenTypeFromToken(token));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (ResourceNotFoundException
        | JWTVerificationException
        | IllegalArgumentException
        | ForbiddenException e) {
      log.error(e.getMessage());
    } finally {
      chain.doFilter(request, response);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
    String username = JWT.decode(token).getSubject();
    var userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);

    return new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
  }
}
