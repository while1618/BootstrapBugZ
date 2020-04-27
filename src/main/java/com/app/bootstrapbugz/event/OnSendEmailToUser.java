package com.app.bootstrapbugz.event;

import com.app.bootstrapbugz.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnSendEmailToUser extends ApplicationEvent {
    private User user;
    private String purpose;

    public OnSendEmailToUser(User user, String purpose) {
        super(user);
        this.user = user;
        this.purpose = purpose;
    }
}
