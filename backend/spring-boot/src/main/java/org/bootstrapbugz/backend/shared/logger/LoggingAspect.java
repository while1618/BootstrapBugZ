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

  private LoggerDTO createLoggerDTO() {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var username = AuthUtil.getAuthName();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var method = request.getMethod();
    final var endpoint = request.getRequestURL().toString();
    return new LoggerDTO(username, ipAddress, method, endpoint);
  }

  @Before(value = "controllerLayer()")
  public void logBefore() {
    final var loggerDTO = createLoggerDTO();
    log.info(
        "{} {} - {} {} called",
        loggerDTO.username(),
        loggerDTO.ipAddress(),
        loggerDTO.method(),
        loggerDTO.endpoint());
  }

  @AfterReturning(value = "controllerLayer()")
  public void logAfter() {
    final var loggerDTO = createLoggerDTO();
    log.info(
        "{} {} - {} {} finished",
        loggerDTO.username(),
        loggerDTO.ipAddress(),
        loggerDTO.method(),
        loggerDTO.endpoint());
  }

  @AfterThrowing(pointcut = "controllerLayer()", throwing = "e")
  public void logException(Throwable e) {
    final var loggerDTO = createLoggerDTO();
    log.error(
        "{} {} - {} {} thrown",
        loggerDTO.username(),
        loggerDTO.ipAddress(),
        loggerDTO.method(),
        loggerDTO.endpoint(),
        e);
  }

  @Before(value = "handleMethodArgumentNotValid()")
  public void beforeHandleMethodArgumentNotValid(JoinPoint joinPoint) {
    final var loggerDTO = createLoggerDTO();
    final var e = (MethodArgumentNotValidException) joinPoint.getArgs()[0];
    log.error(
        "{} {} - {} {} invalid arguments",
        loggerDTO.username(),
        loggerDTO.ipAddress(),
        loggerDTO.method(),
        loggerDTO.endpoint(),
        e);
  }

  @Before(value = "authEntryPoint()")
  public void beforeAuthEntryPoint(JoinPoint joinPoint) {
    final var loggerDTO = createLoggerDTO();
    final var e = (AuthenticationException) joinPoint.getArgs()[2];
    log.error(
        "{} {} - {} {} auth failed",
        loggerDTO.username(),
        loggerDTO.ipAddress(),
        loggerDTO.method(),
        loggerDTO.endpoint(),
        e);
  }
}
