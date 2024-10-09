package org.bootstrapbugz.backend.shared.error.handling;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.shared.error.ErrorMessage;
import org.bootstrapbugz.backend.shared.error.exception.BadRequestException;
import org.bootstrapbugz.backend.shared.error.exception.ConflictException;
import org.bootstrapbugz.backend.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.backend.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.backend.shared.message.service.MessageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
  private final MessageService messageService;

  public CustomExceptionHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @Nonnull MethodArgumentNotValidException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    log.error(e.getMessage(), e);
    final var status = (HttpStatus) statusCode;
    final var errorMessage = new ErrorMessage(status);
    final var result = e.getBindingResult();
    result.getFieldErrors().forEach(error -> errorMessage.addCode(error.getDefaultMessage()));
    result.getGlobalErrors().forEach(error -> errorMessage.addCode(error.getDefaultMessage()));
    return new ResponseEntity<>(errorMessage, setHeaders(), status);
  }

  private ResponseEntity<Object> createError(HttpStatus status, String code) {
    final var errorMessage = new ErrorMessage(status);
    errorMessage.addCode(code);
    return new ResponseEntity<>(errorMessage, setHeaders(), status);
  }

  private HttpHeaders setHeaders() {
    final var header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return header;
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
    log.error(e.getMessage(), e);
    return createError(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
    log.error(e.getMessage(), e);
    return createError(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.error(e.getMessage(), e);
    return createError(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handleConflictException(ConflictException e) {
    log.error(e.getMessage(), e);
    return createError(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
    if (e instanceof DisabledException) {
      final var code = messageService.getMessage("user.notActive");
      log.error(code, e);
      return createError(HttpStatus.FORBIDDEN, code);
    } else if (e instanceof LockedException) {
      final var code = messageService.getMessage("user.lock");
      log.error(code, e);
      return createError(HttpStatus.FORBIDDEN, code);
    } else {
      final var code = messageService.getMessage("auth.unauthorized");
      log.error(code, e);
      return createError(HttpStatus.UNAUTHORIZED, code);
    }
  }

  @ExceptionHandler({AuthorizationDeniedException.class})
  public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
    final var code = messageService.getMessage("auth.forbidden");
    log.error(code, e);
    return createError(HttpStatus.FORBIDDEN, code);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception e) {
    final var code = messageService.getMessage("server.internalError");
    log.error(code, e);
    return createError(HttpStatus.INTERNAL_SERVER_ERROR, code);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      @Nonnull MissingServletRequestParameterException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    final var code = messageService.getMessage("request.parameterMissing");
    log.error(code, e);
    return createError((HttpStatus) statusCode, code);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      @Nonnull HttpRequestMethodNotSupportedException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    final var code = messageService.getMessage("request.methodNotSupported");
    log.error(code, e);
    return createError((HttpStatus) statusCode, code);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @Nonnull HttpMessageNotReadableException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    final var code = messageService.getMessage("request.messageNotReadable");
    log.error(code, e);
    return createError((HttpStatus) statusCode, code);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    final var code = messageService.getMessage("request.parameterTypeMismatch");
    log.error(code, e);
    return createError(HttpStatus.BAD_REQUEST, code);
  }
}
