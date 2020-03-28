package com.app.webapp.event;

import com.app.webapp.model.VerificationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnResendVerificationEmail extends ApplicationEvent {
    private VerificationToken verificationToken;

    public OnResendVerificationEmail(VerificationToken verificationToken) {
        super(verificationToken);
        this.verificationToken = verificationToken;
    }
}
