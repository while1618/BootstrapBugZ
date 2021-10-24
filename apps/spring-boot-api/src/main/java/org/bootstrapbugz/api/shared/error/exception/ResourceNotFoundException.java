package org.bootstrapbugz.api.shared.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class ResourceNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = -6147521296995365840L;
  private final HttpStatus status;

  public ResourceNotFoundException(String message) {
    super(message);
    this.status = HttpStatus.NOT_FOUND;
  }
}
