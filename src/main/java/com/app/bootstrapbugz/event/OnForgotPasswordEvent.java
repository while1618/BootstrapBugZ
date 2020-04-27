package com.app.bootstrapbugz.event;

import com.app.bootstrapbugz.model.user.User;
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
