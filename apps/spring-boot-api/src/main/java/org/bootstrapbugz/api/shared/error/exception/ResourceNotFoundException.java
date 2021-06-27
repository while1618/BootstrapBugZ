package org.bootstrapbugz.api.shared.error.exception;

import java.io.Serial;

import org.bootstrapbugz.api.shared.error.ErrorDomain;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = -6147521296995365840L;
  private final ErrorDomain domain;

  public ResourceNotFoundException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
  }
}
