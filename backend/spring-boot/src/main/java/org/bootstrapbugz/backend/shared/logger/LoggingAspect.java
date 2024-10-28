package org.bootstrapbugz.backend.shared.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
  public LoggingAspect() {
    log.info("LoggingAspect initialized");
  }

  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void controllerLayer() {}

  @Pointcut(
      "execution(public * org.bootstrapbugz.backend.shared.error.handling.CustomExceptionHandler.handleMethodArgumentNotValid(..))")
  public void handleMethodArgumentNotValid() {}

  @Pointcut(
      "execution(public * org.bootstrapbugz.backend.shared.error.handling.CustomAuthenticationEntryPoint.commence(..))")
  public void authEntryPoint() {}

  private Logger createLogger() {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var username = AuthUtil.getAuthName();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var method = request.getMethod();
    final var endpoint = request.getRequestURL().toString();
    return new Logger(username, ipAddress, method, endpoint);
  }

  @Before(value = "controllerLayer()")
  public void logBefore() {
    final var logger = createLogger();
    logger.info("Called");
  }

  @AfterReturning(value = "controllerLayer()")
  public void logAfter() {
    final var logger = createLogger();
    logger.info("Finished");
  }

  @AfterThrowing(pointcut = "controllerLayer()", throwing = "e")
  public void logException(Exception e) {
    final var logger = createLogger();
    logger.error("Thrown", e);
  }

  @Before(value = "handleMethodArgumentNotValid()")
  public void beforeHandleMethodArgumentNotValid(JoinPoint joinPoint) {
    final var e = (MethodArgumentNotValidException) joinPoint.getArgs()[0];
    final var logger = createLogger();
    logger.error("Invalid arguments", e);
  }

  @Before(value = "authEntryPoint()")
  public void beforeAuthEntryPoint(JoinPoint joinPoint) {
    final var e = (AuthenticationException) joinPoint.getArgs()[2];
    final var logger = createLogger();
    logger.error("Auth failed", e);
  }
}
