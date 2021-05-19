package com.app.api.shared.error.exception;

import com.app.api.shared.error.ErrorDomain;
import java.io.Serial;
import lombok.Getter;

@Getter
public class ResourceNotFound extends RuntimeException {
  @Serial private static final long serialVersionUID = -6147521296995365840L;
  private final ErrorDomain domain;

  public ResourceNotFound(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
  }
}
