package org.bootstrapbugz.api.auth.jwt.event;

import lombok.Getter;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

@Getter
public class OnSendJwtEmail extends ApplicationEvent {
  @Serial private static final long serialVersionUID = 6234594744610595282L;
  private final User user;
  private final String token;
  private final JwtUtil.JwtPurpose purpose;

  public OnSendJwtEmail(User user, String token, JwtUtil.JwtPurpose purpose) {
    super(user);
    this.user = user;
    this.token = token;
    this.purpose = purpose;
  }
}
