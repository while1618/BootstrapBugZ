package org.bootstrapbugz.backend.shared.logger;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class Logger {
  public void info(String message) {
    final var logDetails = new LogDetails();
    log.info(
        "REQUEST_ID: {}, USER: {}, IP: {}, ENDPOINT: {} {}, MESSAGE: {}",
        logDetails.getRequestId(),
        logDetails.getUsername(),
        logDetails.getIpAddress(),
        logDetails.getRequestMethod(),
        logDetails.getRequestUrl(),
        message);
  }

  public void error(String message, Exception e) {
    final var logDetails = new LogDetails();
    log.error(
        "REQUEST_ID: {}, USER: {}, IP: {}, ENDPOINT: {} {}, MESSAGE: {}",
        logDetails.getRequestId(),
        logDetails.getUsername(),
        logDetails.getIpAddress(),
        logDetails.getRequestMethod(),
        logDetails.getRequestUrl(),
        message,
        e);
  }

  @Getter
  private static class LogDetails {
    private final String username;
    private final String ipAddress;
    private final String requestId;
    private final String requestMethod;
    private final String requestUrl;

    public LogDetails() {
      final var request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      username = AuthUtil.getAuthName();
      ipAddress = AuthUtil.getUserIpAddress(request);
      requestId = MDC.get("REQUEST_ID");
      requestMethod = request.getMethod();
      requestUrl = request.getRequestURL().toString();
    }
  }
}
