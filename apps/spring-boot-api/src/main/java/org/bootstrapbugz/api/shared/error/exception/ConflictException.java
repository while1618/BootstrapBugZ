package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
  @Serial private static final long serialVersionUID = 8841655774286844538L;
  private final ErrorDomain domain;
  private final HttpStatus status;

  public ConflictException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
    this.status = HttpStatus.CONFLICT;
  }
}
