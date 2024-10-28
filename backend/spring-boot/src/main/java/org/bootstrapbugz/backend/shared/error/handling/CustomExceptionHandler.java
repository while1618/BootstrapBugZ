package org.bootstrapbugz.backend.shared.error.handling;

import jakarta.annotation.Nonnull;
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

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
  private final MessageService messageService;

  public CustomExceptionHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      @Nonnull MethodArgumentNotValidException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
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
    return createError(e.getStatus(), messageService.getMessage(e.getMessage()));
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
    return createError(e.getStatus(), messageService.getMessage(e.getMessage()));
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
    return createError(e.getStatus(), messageService.getMessage(e.getMessage()));
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handleConflictException(ConflictException e) {
    return createError(e.getStatus(), messageService.getMessage(e.getMessage()));
  }

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
    if (e instanceof DisabledException) {
      return createError(HttpStatus.FORBIDDEN, messageService.getMessage("user.notActive"));
    } else if (e instanceof LockedException) {
      return createError(HttpStatus.FORBIDDEN, messageService.getMessage("user.lock"));
    } else {
      return createError(HttpStatus.UNAUTHORIZED, messageService.getMessage("auth.unauthorized"));
    }
  }

  @ExceptionHandler({AuthorizationDeniedException.class})
  public ResponseEntity<Object> handleAuthorizationDeniedException(
      AuthorizationDeniedException ignored) {
    return createError(HttpStatus.FORBIDDEN, messageService.getMessage("auth.forbidden"));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception ignored) {
    return createError(
        HttpStatus.INTERNAL_SERVER_ERROR, messageService.getMessage("server.internalError"));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      @Nonnull MissingServletRequestParameterException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    return createError(
        (HttpStatus) statusCode, messageService.getMessage("request.parameterMissing"));
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      @Nonnull HttpRequestMethodNotSupportedException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    return createError(
        (HttpStatus) statusCode, messageService.getMessage("request.methodNotSupported"));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @Nonnull HttpMessageNotReadableException e,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatusCode statusCode,
      @Nonnull WebRequest request) {
    return createError(
        (HttpStatus) statusCode, messageService.getMessage("request.messageNotReadable"));
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ignored) {
    return createError(
        HttpStatus.BAD_REQUEST, messageService.getMessage("request.parameterTypeMismatch"));
  }
}
