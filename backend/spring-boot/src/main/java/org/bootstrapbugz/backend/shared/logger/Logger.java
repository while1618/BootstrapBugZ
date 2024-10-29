package org.bootstrapbugz.backend.shared.logger;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class Logger {
  public void info(String message) {
    final var logDetails = new LogDetails();
    log.info(
        "USER: {}, IP: {}, REQUEST: {} {}, MESSAGE: {}",
        logDetails.getUsername(),
        logDetails.getIpAddress(),
        logDetails.getRequestMethod(),
        logDetails.getRequestUrl(),
        message);
  }

  public void error(String message, Exception e) {
    final var logDetails = new LogDetails();
    log.error(
        "USER: {}, IP: {}, REQUEST: {} {}, MESSAGE: {}",
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
    private final String requestMethod;
    private final String requestUrl;

    public LogDetails() {
      final var request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      username = AuthUtil.getAuthName();
      ipAddress = AuthUtil.getUserIpAddress(request);
      requestMethod = request.getMethod();
      requestUrl = request.getRequestURL().toString();
    }
  }
}
