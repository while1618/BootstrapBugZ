package com.app.webapp.error.handling;

import com.app.webapp.error.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
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
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static String GLOBAL_DOMAIN = "global";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status);
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addError(error.getObjectName() + "." + error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorResponse.addError(error.getObjectName(), error.getDefaultMessage());
        }
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    private ResponseEntity<Object> createErrorsResponseEntity(String domain, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status, domain, message);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
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

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return createErrorsResponseEntity(
                GLOBAL_DOMAIN,
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<Object> userAlreadyExist(UserAlreadyExistException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> userNotFound(UserNotFoundException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserNotAuthorizedException.class})
    public ResponseEntity<Object> userNotAuthorized(UserNotAuthorizedException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({VerificationTokenNotValidException.class})
    public ResponseEntity<Object> verificationTokenNotValid(VerificationTokenNotValidException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({VerificationTokenNotFoundException.class})
    public ResponseEntity<Object> verificationTokenNotFound(VerificationTokenNotFoundException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<Object> employeeNotFound(EmployeeNotFoundException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DepartmentNotFoundException.class})
    public ResponseEntity<Object> departmentNotFound(DepartmentNotFoundException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({LocationNotFoundException.class})
    public ResponseEntity<Object> locationNotFound(LocationNotFoundException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({LoginException.class})
    public ResponseEntity<Object> locationNotFound(LoginException ex) {
        return createErrorsResponseEntity(ex.getDomain(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception ex) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<Object> expiredJwtException(ExpiredJwtException ex) {
        return createErrorsResponseEntity(GLOBAL_DOMAIN, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
