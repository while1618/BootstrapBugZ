package com.app.webapp.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ResendConfirmationEmailRequest {
    @NotEmpty(message = "{username.notEmpty}")
    private String username;
}
