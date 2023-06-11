package org.bootstrapbugz.api.shared.error.handling;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class CustomFilterExceptionHandler {
  private CustomFilterExceptionHandler() {}

  public static void handleException(
      HttpServletResponse response, String message, HttpStatus status) {
    try {
      final var errorMessage = new ErrorMessage(status);
      errorMessage.addDetails(message);
      response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
      response.setStatus(status.value());
      response.getOutputStream().println(errorMessage.toString());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
