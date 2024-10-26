package org.bootstrapbugz.backend.shared.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
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

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerLayer() {}

  @Pointcut(
      "execution(public * org.bootstrapbugz.backend.shared.error.handling.CustomExceptionHandler.handleMethodArgumentNotValid(..))")
  public void handleMethodArgumentNotValid() {}

  @Before(value = "controllerLayer()")
  public void logBefore(JoinPoint joinPoint) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    final var methodName = joinPoint.getSignature().getName();
    log.info(">> {} - {}.{}() called", ipAddress, clazz, methodName);
  }

  @AfterReturning(value = "controllerLayer()")
  public void logAfter(JoinPoint joinPoint) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    final var methodName = joinPoint.getSignature().getName();
    log.info("<< {} - {}.{}() finished", ipAddress, clazz, methodName);
  }

  @AfterThrowing(pointcut = "controllerLayer()", throwing = "e")
  public void logException(JoinPoint joinPoint, Throwable e) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    final var methodName = joinPoint.getSignature().getName();
    log.error("<< {} - {}.{}() thrown", ipAddress, clazz, methodName, e);
  }

  @Before(value = "handleMethodArgumentNotValid()")
  public void beforeHandleMethodArgumentNotValid(JoinPoint joinPoint) {
    final var request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var e = (MethodArgumentNotValidException) joinPoint.getArgs()[0];
    final var objectName = e.getBindingResult().getObjectName();
    log.error("<< {} - {} invalid arguments", ipAddress, objectName, e);
  }
}
