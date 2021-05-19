package com.app.api.jwt.event;

import com.app.api.jwt.util.JwtPurpose;
import com.app.api.user.model.User;
import java.io.Serial;
import lombok.Getter;
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
    this.token = token;
    this.purpose = purpose;
  }
}
