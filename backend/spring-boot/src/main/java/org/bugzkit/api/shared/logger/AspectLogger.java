package org.bugzkit.api.shared.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AspectLogger {
  private final CustomLogger customLogger;

  public AspectLogger(CustomLogger customLogger) {
    log.info("AspectLogger Initialized");
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
