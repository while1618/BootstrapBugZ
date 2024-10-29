package org.bootstrapbugz.backend.shared.logger;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@NoArgsConstructor
public class Logger {
  public void info(String message) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var username = AuthUtil.getAuthName();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var method = request.getMethod();
    final var endpoint = request.getRequestURL().toString();
    log.info(
        "USER: {}, IP: {}, REQUEST: {} {}, MESSAGE: {}",
        username,
        ipAddress,
        method,
        endpoint,
        message);
  }

  public void error(String message, Exception e) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var username = AuthUtil.getAuthName();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var method = request.getMethod();
    final var endpoint = request.getRequestURL().toString();
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
