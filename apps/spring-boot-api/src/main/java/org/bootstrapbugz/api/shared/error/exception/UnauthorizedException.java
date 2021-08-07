package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;
import lombok.Getter;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedException extends RuntimeException {
  @Serial private static final long serialVersionUID = -8525346226722308705L;
  private final ErrorDomain domain;
  private final HttpStatus status;

  public UnauthorizedException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
    this.status = HttpStatus.UNAUTHORIZED;
  }
}
