package org.bootstrapbugz.backend.shared.error.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
  @Serial private static final long serialVersionUID = 6438756026918720197L;
  private final HttpStatus status;

  public ForbiddenException(String message) {
    super(message);
    this.status = HttpStatus.FORBIDDEN;
  }
}
