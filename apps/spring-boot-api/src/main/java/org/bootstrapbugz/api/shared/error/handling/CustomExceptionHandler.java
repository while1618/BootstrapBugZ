package org.bootstrapbugz.api.shared.error.handling;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
  @Nonnull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    final var errorResponse = new ErrorResponse(status);
    final var result = ex.getBindingResult();
    result
        .getFieldErrors()
        .forEach(error -> errorResponse.addError(error.getField(), error.getDefaultMessage()));
    result
        .getGlobalErrors()
        .forEach(
            error -> {
              final var errorObject = Objects.requireNonNull(error.getArguments())[1];
              errorResponse.addError(
                  (errorObject == null) ? ErrorDomain.GLOBAL.getValue() : errorObject.toString(),
                  error.getDefaultMessage());
            });
    return new ResponseEntity<>(errorResponse, headers, status);
  }

  private ResponseEntity<Object> createErrorResponseEntity(
      ErrorDomain domain, String message, HttpStatus status) {
    final var errorResponse = new ErrorResponse(status, domain, message);
    return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
    return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
    return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    return createErrorResponseEntity(ErrorDomain.AUTH, ex.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handleConflictException(ConflictException ex) {
    return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception ex) {
    return createErrorResponseEntity(
        ErrorDomain.GLOBAL, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  @Nonnull
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    return createErrorResponseEntity(
        ErrorDomain.GLOBAL, ex.getParameterName() + " parameter is missing", status);
  }

  @Override
  @Nonnull
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    final var builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods())
        .forEach(t -> builder.append(t).append(" "));
    return createErrorResponseEntity(ErrorDomain.GLOBAL, builder.toString(), status);
  }

  @Override
  @Nonnull
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    return createErrorResponseEntity(
        ErrorDomain.GLOBAL, ex.getMostSpecificCause().getMessage(), status);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return createErrorResponseEntity(
        ErrorDomain.GLOBAL,
        ex.getName()
            + " should be of type "
            + Objects.requireNonNull(ex.getRequiredType()).getName(),
        HttpStatus.BAD_REQUEST);
  }
}
