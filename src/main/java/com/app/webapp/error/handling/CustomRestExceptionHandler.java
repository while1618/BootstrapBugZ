package com.app.webapp.error.handling;

import com.app.webapp.error.exception.*;
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

import java.util.*;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    private static String GLOBAL_DOMAIN = "global";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status);
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            apiError.addError(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            apiError.addError(error.getObjectName(), error.getDefaultMessage());
        }
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    private ResponseEntity<Object> createErrorsResponseEntity(String domain, String message, HttpStatus status) {
        ApiError apiError = new ApiError(status, domain, message);
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getParameterName() + " parameter is missing", status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));
        return createErrorsResponseEntity(GLOBAL_DOMAIN, builder.toString(), status);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return createErrorsResponseEntity(
                GLOBAL_DOMAIN,
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ UserAlreadyExistException.class, NotValidVerificationTokenException.class })
    public ResponseEntity<Object> badRequestCustomExceptions(RuntimeException ex) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            DepartmentNotFoundException.class, EmployeeNotFoundException.class, LocationNotFoundException.class,
            UserNotFoundException.class, VerificationTokenNotFoundException.class
    })
    public ResponseEntity<Object> notFoundCustomExceptions(RuntimeException ex) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ UserNotAuthorizedException.class })
    public ResponseEntity<Object> notAuthorizedCustomExceptions(RuntimeException ex) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
