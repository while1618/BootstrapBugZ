package org.bootstrapbugz.api.shared.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class ConflictException extends RuntimeException {
  @Serial private static final long serialVersionUID = 8841655774286844538L;
  private final HttpStatus status;
  private final String field;

  public ConflictException(String field, String message) {
    super(message);
    this.status = HttpStatus.CONFLICT;
    this.field = field;
  }
}
