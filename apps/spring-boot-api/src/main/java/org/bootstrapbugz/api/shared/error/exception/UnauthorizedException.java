package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;

import org.bootstrapbugz.api.shared.error.ErrorDomain;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
  @Serial private static final long serialVersionUID = -8525346226722308705L;
  private final ErrorDomain domain;

  public UnauthorizedException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
  }
}
