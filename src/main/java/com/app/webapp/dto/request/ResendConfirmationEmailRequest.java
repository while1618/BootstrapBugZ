package com.app.webapp.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ResendConfirmationEmailRequest {
    @NotEmpty(message = "{login.usernameOrEmail.notEmpty}")
    private String usernameOrEmail;
}
