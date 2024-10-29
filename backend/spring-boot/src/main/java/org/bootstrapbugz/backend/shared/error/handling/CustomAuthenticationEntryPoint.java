package org.bootstrapbugz.backend.shared.error.handling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.bootstrapbugz.backend.shared.error.ErrorMessage;
import org.bootstrapbugz.backend.shared.logger.CustomLogger;
import org.bootstrapbugz.backend.shared.message.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final MessageService messageService;
  private final CustomLogger customLogger;

  public CustomAuthenticationEntryPoint(MessageService messageService, CustomLogger customLogger) {
    this.messageService = messageService;
    this.customLogger = customLogger;
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
      throws IOException {
    customLogger.error("Auth failed", e);
    final var errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED);
    errorMessage.addCode(messageService.getMessage("auth.unauthorized"));
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getOutputStream().println(errorMessage.toString());
  }
}
