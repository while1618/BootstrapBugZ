package org.bootstrapbugz.api.shared.error.handling;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import javax.annotation.Nonnull;
import java.util.Objects;

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
        .forEach(error -> errorResponse.addDetails(error.getField(), error.getDefaultMessage()));
    result.getGlobalErrors().forEach(error -> errorResponse.addDetails(error.getDefaultMessage()));
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return new ResponseEntity<>(errorResponse, headers, status);
  }

  private ResponseEntity<Object> createErrorResponseEntityWithoutField(
      HttpStatus status, String message) {
    final var errorResponse = new ErrorResponse(status);
    errorResponse.addDetails(message);
    final var header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return new ResponseEntity<>(errorResponse, header, status);
  }

  private ResponseEntity<Object> createErrorResponseEntityWithField(
      HttpStatus status, String field, String message) {
    final var errorResponse = new ErrorResponse(status);
    errorResponse.addDetails(field, message);
    final var header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return new ResponseEntity<>(errorResponse, header, status);
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    return createErrorResponseEntityWithField(ex.getStatus(), ex.getField(), ex.getMessage());
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
    return createErrorResponseEntityWithoutField(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
    return createErrorResponseEntityWithoutField(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    return createErrorResponseEntityWithoutField(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return createErrorResponseEntityWithoutField(ex.getStatus(), ex.getMessage());
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handleConflictException(ConflictException ex) {
    return createErrorResponseEntityWithField(ex.getStatus(), ex.getField(), ex.getMessage());
  }

  @ExceptionHandler({JWTVerificationException.class})
  public ResponseEntity<Object> handleJwtVerificationException(JWTVerificationException ex) {
    return createErrorResponseEntityWithoutField(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleGlobalException(Exception ex) {
    return createErrorResponseEntityWithoutField(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @Override
  @Nonnull
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    return createErrorResponseEntityWithoutField(
        status, ex.getParameterName() + " parameter is missing");
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
    return createErrorResponseEntityWithoutField(status, builder.toString());
  }

  @Override
  @Nonnull
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      @Nonnull HttpHeaders headers,
      @Nonnull HttpStatus status,
      @Nonnull WebRequest request) {
    return createErrorResponseEntityWithoutField(status, ex.getMostSpecificCause().getMessage());
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return createErrorResponseEntityWithoutField(
        HttpStatus.BAD_REQUEST,
        ex.getName()
            + " should be of type "
            + Objects.requireNonNull(ex.getRequiredType()).getName());
  }
}
