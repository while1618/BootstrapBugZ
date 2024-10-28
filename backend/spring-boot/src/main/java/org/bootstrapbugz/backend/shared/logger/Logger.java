package org.bootstrapbugz.backend.shared.logger;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Logger {
  private String username;
  private String ipAddress;
  private String method;
  private String endpoint;

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
