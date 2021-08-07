package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
  @Serial private static final long serialVersionUID = -6237654540916338509L;
  private final ErrorDomain domain;
  private final HttpStatus status;

  public BadRequestException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
    this.status = HttpStatus.BAD_REQUEST;
  }
}
