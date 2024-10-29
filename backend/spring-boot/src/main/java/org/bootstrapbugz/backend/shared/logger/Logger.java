package org.bootstrapbugz.backend.shared.logger;

import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class Logger {
  private final String username;
  private final String ipAddress;
  private final String method;
  private final String endpoint;

  public Logger() {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    username = AuthUtil.getAuthName();
    ipAddress = AuthUtil.getUserIpAddress(request);
    method = request.getMethod();
    endpoint = request.getRequestURL().toString();
  }

  public void info(String message) {
    log.info(
        "USER: {}, IP: {}, REQUEST: {} {}, MESSAGE: {}",
        username,
        ipAddress,
        method,
        endpoint,
        message);
  }

  public void error(String message, Exception e) {
    log.error(
        "USER: {}, IP: {}, REQUEST: {} {}, MESSAGE: {}",
        username,
        ipAddress,
        method,
        endpoint,
        message,
        e);
  }
}
