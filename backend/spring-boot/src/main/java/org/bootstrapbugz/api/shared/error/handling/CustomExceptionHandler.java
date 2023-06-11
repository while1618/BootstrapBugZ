package org.bootstrapbugz.api.shared.error.handling;

import java.util.Objects;
import lombok.NonNull;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode statusCode,
      @NonNull WebRequest request) {
    final var status = (HttpStatus) statusCode;
    final var errorMessage = new ErrorMessage(status);
    final var result = ex.getBindingResult();
    result
        .getFieldErrors()
        .forEach(error -> errorMessage.addDetails(error.getField(), error.getDefaultMessage()));
    result.getGlobalErrors().forEach(error -> errorMessage.addDetails(error.getDefaultMessage()));
    return new ResponseEntity<>(errorMessage, setHeaders(), status);
  }

  private ResponseEntity<Object> createError(HttpStatus status, String message, String field) {
    final var errorMessage = new ErrorMessage(status);
    if (field != null) errorMessage.addDetails(field, message);
    else errorMessage.addDetails(message);
    return new ResponseEntity<>(errorMessage, setHeaders(), status);
  }

  private HttpHeaders setHeaders() {
    final var header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return header;
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    return createError(ex.getStatus(), ex.getMessage(), ex.getField());
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
    return createError(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
    return createError(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    return createError(HttpStatus.FORBIDDEN, ex.getMessage(), null);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return createError(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handleConflictException(ConflictException ex) {
    return createError(ex.getStatus(), ex.getMessage(), ex.getField());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception ex) {
    return createError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode statusCode,
      @NonNull WebRequest request) {
    return createError(
        (HttpStatus) statusCode, ex.getParameterName() + " parameter is missing", null);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode statusCode,
      @NonNull WebRequest request) {
    final var builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods())
        .forEach(t -> builder.append(t).append(" "));
    return createError((HttpStatus) statusCode, builder.toString(), null);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode statusCode,
      @NonNull WebRequest request) {
    return createError((HttpStatus) statusCode, ex.getMostSpecificCause().getMessage(), null);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return createError(
        HttpStatus.BAD_REQUEST,
        ex.getName()
            + " should be of type "
            + Objects.requireNonNull(ex.getRequiredType()).getName(),
        null);
  }
}
