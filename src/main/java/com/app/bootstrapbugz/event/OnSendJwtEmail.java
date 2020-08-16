package com.app.bootstrapbugz.event;

import com.app.bootstrapbugz.constant.JwtPurpose;
import com.app.bootstrapbugz.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnSendJwtEmail extends ApplicationEvent {
    private User user;
    private String token;
    private JwtPurpose purpose;

    public OnSendJwtEmail(User user, String token, JwtPurpose purpose) {
        super(user);
        this.user = user;
        this.token = token;
        this.purpose = purpose;
    }
}
