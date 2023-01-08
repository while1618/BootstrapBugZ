package org.bootstrapbugz.api.shared.error.handling;

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.util.Objects;
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
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    final var status = (HttpStatus) statusCode;
    final var errorResponse = new ErrorMessage(status);
    final var result = ex.getBindingResult();
    result
        .getFieldErrors()
        .forEach(error -> errorResponse.addDetails(error.getField(), error.getDefaultMessage()));
    result.getGlobalErrors().forEach(error -> errorResponse.addDetails(error.getDefaultMessage()));
    return new ResponseEntity<>(errorResponse, setHeaders(), status);
  }

  private HttpHeaders setHeaders() {
    final var header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    return header;
  }

  private ResponseEntity<Object> createErrorResponseEntityWithoutField(
      HttpStatus status, String message) {
    final var errorResponse = new ErrorMessage(status);
    errorResponse.addDetails(message);
    return new ResponseEntity<>(errorResponse, setHeaders(), status);
  }

  private ResponseEntity<Object> createErrorResponseEntityWithField(
      HttpStatus status, String field, String message) {
    final var errorResponse = new ErrorMessage(status);
    errorResponse.addDetails(field, message);
    return new ResponseEntity<>(errorResponse, setHeaders(), status);
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
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    return createErrorResponseEntityWithoutField(
        (HttpStatus) statusCode, ex.getParameterName() + " parameter is missing");
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    final var builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods())
        .forEach(t -> builder.append(t).append(" "));
    return createErrorResponseEntityWithoutField((HttpStatus) statusCode, builder.toString());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    return createErrorResponseEntityWithoutField(
        (HttpStatus) statusCode, ex.getMostSpecificCause().getMessage());
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