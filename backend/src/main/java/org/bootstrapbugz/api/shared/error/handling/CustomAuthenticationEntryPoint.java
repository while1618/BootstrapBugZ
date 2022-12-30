package org.bootstrapbugz.api.shared.error.handling;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
    try {
      final var errorResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED);
      errorResponse.addDetails(e.getMessage());
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getOutputStream().println(errorResponse.toString());
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
  }
}
