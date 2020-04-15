package com.app.webapp.event;

import com.app.webapp.model.AuthToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnResendVerificationEmailEvent extends ApplicationEvent {
    private AuthToken authToken;

    public OnResendVerificationEmailEvent(AuthToken authToken) {
        super(authToken);
        this.authToken = authToken;
    }
}
