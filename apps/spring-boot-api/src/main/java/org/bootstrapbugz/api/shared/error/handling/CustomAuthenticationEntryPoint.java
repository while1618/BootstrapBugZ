package org.bootstrapbugz.api.shared.error.handling;

import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
    try {
      final var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED);
      errorResponse.addDetails(e.getMessage());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getOutputStream().println(errorResponse.toString());
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
  }
}
