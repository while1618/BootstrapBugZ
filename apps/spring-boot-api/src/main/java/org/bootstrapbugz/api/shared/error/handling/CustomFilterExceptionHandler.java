package org.bootstrapbugz.api.shared.error.handling;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class CustomFilterExceptionHandler {
  private CustomFilterExceptionHandler() {}

  public static void handleException(HttpServletResponse response, String message, HttpStatus status) {
    try {
      final var errorResponse = new ErrorResponse(status, ErrorDomain.AUTH, message);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(status.value());
      response.getOutputStream().println(errorResponse.toString());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
