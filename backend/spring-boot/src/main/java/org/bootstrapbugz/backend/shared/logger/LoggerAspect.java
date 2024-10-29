package org.bootstrapbugz.backend.shared.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
  private final CustomLogger customLogger;

  public LoggerAspect(CustomLogger customLogger) {
    log.info("LoggerAspect Initialized");
    this.customLogger = customLogger;
  }

  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void controllerLayer() {}

  @Before(value = "controllerLayer()")
  public void logBefore() {
    customLogger.info("Called");
  }

  @AfterReturning(value = "controllerLayer()")
  public void logAfter() {
    customLogger.info("Finished");
  }
}
