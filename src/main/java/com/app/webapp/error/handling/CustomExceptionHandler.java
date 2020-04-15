package com.app.webapp.error.handling;

import com.app.webapp.dto.response.ErrorResponse;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.BadRequestException;
import com.app.webapp.error.exception.LoginException;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.error.exception.AuthTokenNotValidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status);
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addError(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorResponse.addError(ErrorDomains.GLOBAL, error.getDefaultMessage());
        }
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    private ResponseEntity<Object> createErrorResponseEntity(String domain, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status, domain, message);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
    }

    @ExceptionHandler({ResourceNotFound.class})
    public ResponseEntity<Object> resourceNotFound(ResourceNotFound ex) {
        return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({LoginException.class})
    public ResponseEntity<Object> login(LoginException ex) {
        return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthTokenNotValidException.class})
    public ResponseEntity<Object> authTokenNotValid(AuthTokenNotValidException ex) {
        return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> badRequest(BadRequestException ex) {
        return createErrorResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception ex) {
        return createErrorResponseEntity(ErrorDomains.GLOBAL, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return createErrorResponseEntity(ErrorDomains.GLOBAL, ex.getParameterName() + " parameter is missing", status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));
        return createErrorResponseEntity(ErrorDomains.GLOBAL, builder.toString(), status);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return createErrorResponseEntity(
                ErrorDomains.GLOBAL,
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName(),
                HttpStatus.BAD_REQUEST);
    }
}
