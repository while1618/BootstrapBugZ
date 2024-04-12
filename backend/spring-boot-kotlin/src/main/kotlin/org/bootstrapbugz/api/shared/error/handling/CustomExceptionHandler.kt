package org.bootstrapbugz.api.shared.error.handling

import java.util.Objects
import java.util.function.Consumer
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.shared.error.exception.ConflictException
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.shared.payload.dto.ErrorMessage
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomExceptionHandler(private val messageService: MessageService) :
  ResponseEntityExceptionHandler() {
  override fun handleMethodArgumentNotValid(
    ex: MethodArgumentNotValidException,
    headers: HttpHeaders,
    statusCode: HttpStatusCode,
    request: WebRequest
  ): ResponseEntity<Any> {
    val status = statusCode as HttpStatus
    val errorMessage = ErrorMessage(status)
    val result = ex.bindingResult
    result.fieldErrors.forEach(
      Consumer { error: FieldError -> errorMessage.addDetails(error.field, error.defaultMessage) }
    )
    result.globalErrors.forEach(
      Consumer { error: ObjectError -> errorMessage.addDetails(error.defaultMessage) }
    )
    return ResponseEntity(errorMessage.toJson(), setHeaders(), status)
  }

  private fun createError(
    status: HttpStatus,
    message: String?,
    field: String?
  ): ResponseEntity<Any> {
    val errorMessage = ErrorMessage(status)
    if (field != null) errorMessage.addDetails(field, message!!)
    else errorMessage.addDetails(message!!)
    return ResponseEntity(errorMessage.toJson(), setHeaders(), status)
  }

  private fun setHeaders(): HttpHeaders {
    val header = HttpHeaders()
    header.contentType = MediaType.APPLICATION_PROBLEM_JSON
    return header
  }

  @ExceptionHandler(BadRequestException::class)
  fun handleBadRequestException(ex: BadRequestException): ResponseEntity<Any> {
    return createError(ex.status, ex.message, ex.field)
  }

  @ExceptionHandler(UnauthorizedException::class)
  fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<Any> {
    return createError(ex.status, ex.message, null)
  }

  @ExceptionHandler(ForbiddenException::class)
  fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<Any> {
    return createError(ex.status, ex.message, null)
  }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<Any> {
    return createError(HttpStatus.FORBIDDEN, ex.message, null)
  }

  @ExceptionHandler(ResourceNotFoundException::class)
  fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<Any> {
    return createError(ex.status, ex.message, null)
  }

  @ExceptionHandler(ConflictException::class)
  fun handleConflictException(ex: ConflictException): ResponseEntity<Any> {
    return createError(ex.status, ex.message, ex.field)
  }

  @ExceptionHandler(AuthenticationException::class)
  fun handleAuthenticationException(ex: AuthenticationException?): ResponseEntity<Any> {
    return when (ex) {
      is DisabledException ->
        createError(HttpStatus.FORBIDDEN, messageService.getMessage("user.notActive"), null)
      is LockedException ->
        createError(HttpStatus.FORBIDDEN, messageService.getMessage("user.lock"), null)
      else -> createError(HttpStatus.UNAUTHORIZED, messageService.getMessage("auth.invalid"), null)
    }
  }

  @ExceptionHandler(Exception::class)
  fun handleGlobalException(ex: Exception): ResponseEntity<Any> {
    return createError(HttpStatus.INTERNAL_SERVER_ERROR, ex.message, null)
  }

  override fun handleMissingServletRequestParameter(
    ex: MissingServletRequestParameterException,
    headers: HttpHeaders,
    statusCode: HttpStatusCode,
    request: WebRequest
  ): ResponseEntity<Any> {
    return createError(statusCode as HttpStatus, ex.parameterName + " parameter is missing", null)
  }

  override fun handleHttpRequestMethodNotSupported(
    ex: HttpRequestMethodNotSupportedException,
    headers: HttpHeaders,
    statusCode: HttpStatusCode,
    request: WebRequest
  ): ResponseEntity<Any> {
    val builder = StringBuilder()
    builder.append(ex.method)
    builder.append(" method is not supported for this request. Supported methods are ")
    Objects.requireNonNull(ex.supportedHttpMethods)
      .forEach(Consumer { t: HttpMethod? -> builder.append(t).append(" ") })
    return createError(statusCode as HttpStatus, builder.toString(), null)
  }

  override fun handleHttpMessageNotReadable(
    ex: HttpMessageNotReadableException,
    headers: HttpHeaders,
    statusCode: HttpStatusCode,
    request: WebRequest
  ): ResponseEntity<Any> {
    return createError(statusCode as HttpStatus, ex.mostSpecificCause.message, null)
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleMethodArgumentTypeMismatch(
    ex: MethodArgumentTypeMismatchException
  ): ResponseEntity<Any> {
    val expectedType = ex.requiredType?.name ?: "unknown type"
    val errorMessage = "${ex.name} should be of type $expectedType"

    return createError(HttpStatus.BAD_REQUEST, errorMessage, null)
  }
}
