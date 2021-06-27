package org.bootstrapbugz.api.auth.event;

import java.io.Serial;
import lombok.Getter;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnSendJwtEmail extends ApplicationEvent {
  @Serial private static final long serialVersionUID = 6234594744610595282L;
  private final User user;
  private final String token;
  private final JwtPurpose purpose;

  public OnSendJwtEmail(User user, String token, JwtPurpose purpose) {
    super(user);
    this.user = user;
    this.token = JwtUtil.removeTokenTypeFromToken(token);
    this.purpose = purpose;
  }
}
