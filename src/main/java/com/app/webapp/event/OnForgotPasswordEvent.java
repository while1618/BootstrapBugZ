package com.app.webapp.event;

import com.app.webapp.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnForgotPasswordEvent extends ApplicationEvent {
    private User user;

    public OnForgotPasswordEvent(User user) {
        super(user);
        this.user = user;
    }
}
