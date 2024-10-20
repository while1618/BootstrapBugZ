package org.bootstrapbugz.backend.shared.logger;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
  public LoggingAspect() {
    log.info("LoggingAspect initialized");
  }

  @Pointcut("execution(public * org.bootstrapbugz.backend..*.*(..)))")
  public void allPublicMethods() {}

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerLayer() {}

  @Pointcut("within(@org.springframework.stereotype.Service *)")
  public void serviceLayer() {}

  @Pointcut(
      "execution(public * org.bootstrapbugz.backend.shared.error.handling.CustomExceptionHandler.handleMethodArgumentNotValid(..))")
  public void handleMethodArgumentNotValid() {}

  @Before(value = "serviceLayer() || controllerLayer()")
  public void logBefore(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    String clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    log.info(">> {}.{}() - {}", clazz, methodName, Arrays.toString(args));
  }

  @AfterReturning(value = "serviceLayer() || controllerLayer()", returning = "result")
  public void logAfter(JoinPoint joinPoint, Object result) {
    String clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    log.info("<< {}.{}() - {}", clazz, methodName, result);
  }

  @AfterThrowing(pointcut = "controllerLayer()", throwing = "exception")
  public void logException(JoinPoint joinPoint, Throwable exception) {
    String clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    log.error("<< {}.{}() - {}", clazz, methodName, exception.getMessage(), exception);
  }

  @Before(value = "handleMethodArgumentNotValid()")
  public void beforeHandleMethodArgumentNotValid(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    MethodArgumentNotValidException e = (MethodArgumentNotValidException) args[0];
    log.error("<< Invalid arguments for object - {}", e.getBindingResult().getObjectName(), e);
  }
}
