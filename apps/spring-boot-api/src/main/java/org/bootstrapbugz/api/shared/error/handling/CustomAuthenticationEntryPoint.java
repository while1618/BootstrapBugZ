package org.bootstrapbugz.api.shared.error.handling;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
    try {
      final ErrorResponse errorResponse =
          new ErrorResponse(
              HttpStatus.FORBIDDEN, ErrorDomain.AUTH, HttpStatus.FORBIDDEN.getReasonPhrase());
      response.setContentType("application/json");
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.getOutputStream().println(errorResponse.toString());
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
  }
}
