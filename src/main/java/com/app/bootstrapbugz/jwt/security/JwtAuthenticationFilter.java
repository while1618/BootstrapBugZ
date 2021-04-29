package com.app.bootstrapbugz.jwt.security;

import com.app.bootstrapbugz.auth.request.LoginRequest;
import com.app.bootstrapbugz.common.constants.Path;
import com.app.bootstrapbugz.common.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.common.error.handling.CustomFilterExceptionHandler;
import com.app.bootstrapbugz.jwt.util.JwtPurpose;
import com.app.bootstrapbugz.jwt.util.JwtUtilities;
import com.app.bootstrapbugz.user.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final JwtUtilities jwtUtilities;
  private final AuthenticationManager authenticationManager;
  private final MessageSource messageSource;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      JwtUtilities jwtUtilities,
      MessageSource messageSource) {
    this.authenticationManager = authenticationManager;
    this.jwtUtilities = jwtUtilities;
    this.messageSource = messageSource;
    this.setFilterProcessesUrl(Path.AUTH + "/login");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      LoginRequest loginRequest =
          new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(), loginRequest.getPassword(), new ArrayList<>());
      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException | AuthenticationException | ResourceNotFound e) {
      CustomFilterExceptionHandler.handleException(response, getMessageBasedOnException(e));
    }
    return null;
  }

  private String getMessageBasedOnException(Exception e) {
    String errorMessage;
    if (e instanceof DisabledException)
      errorMessage =
          messageSource.getMessage("user.notActivated", null, LocaleContextHolder.getLocale());
    else if (e instanceof LockedException)
      errorMessage = messageSource.getMessage("user.locked", null, LocaleContextHolder.getLocale());
    else
      errorMessage =
          messageSource.getMessage("login.invalid", null, LocaleContextHolder.getLocale());
    return errorMessage;
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication auth) {
    String token =
        jwtUtilities.createToken(
            (UserPrincipal) auth.getPrincipal(), JwtPurpose.ACCESSING_RESOURCES);
    response.addHeader(JwtUtilities.HEADER, JwtUtilities.BEARER + token);
    response.addHeader("access-control-expose-headers", JwtUtilities.HEADER);
  }
}
