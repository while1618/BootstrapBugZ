package com.app.bootstrapbugz.common.error.exception;

import com.app.bootstrapbugz.common.error.ErrorDomain;
import java.io.Serial;
import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
  @Serial private static final long serialVersionUID = 6438756026918720197L;
  private final ErrorDomain domain;

  public ForbiddenException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
  }
}
