package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;
import lombok.Getter;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
  @Serial private static final long serialVersionUID = 6438756026918720197L;
  private final ErrorDomain domain;
  private final HttpStatus status;

  public ForbiddenException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
    this.status = HttpStatus.FORBIDDEN;
  }
}
