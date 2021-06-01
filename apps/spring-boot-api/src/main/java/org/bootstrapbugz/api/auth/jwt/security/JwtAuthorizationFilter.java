package org.bootstrapbugz.api.auth.jwt.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;

import org.bootstrapbugz.api.auth.jwt.util.JwtPurpose;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtilities;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private final JwtUtilities jwtUtilities;
  private final UserDetailsService userDetailsService;

  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager,
      JwtUtilities jwtUtilities,
      @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
    super(authenticationManager);
    this.jwtUtilities = jwtUtilities;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = request.getHeader(JwtUtilities.HEADER);
    if (token == null || !token.startsWith(JwtUtilities.BEARER)) {
      chain.doFilter(request, response);
      return;
    }
    try {
      UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    } catch (ResourceNotFound | JWTVerificationException | IllegalArgumentException e) {
      log.error(e.getMessage());
    } finally {
      chain.doFilter(request, response);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
    String username = jwtUtilities.getSubject(token);
    UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
    jwtUtilities.checkToken(token, userPrincipal, JwtPurpose.ACCESSING_RESOURCES);

    return new UsernamePasswordAuthenticationToken(
        userPrincipal.getUsername(), null, userPrincipal.getAuthorities());
  }
}
