package org.bootstrapbugz.api.shared.error.exception;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import java.io.Serial;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
  @Serial private static final long serialVersionUID = -6237654540916338509L;
  private final ErrorDomain domain;

  public BadRequestException(String message, ErrorDomain domain) {
    super(message);
    this.domain = domain;
  }
}
