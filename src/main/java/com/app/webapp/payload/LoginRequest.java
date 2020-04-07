package com.app.webapp.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginRequest {
    @NotEmpty(message = "{login.usernameOrEmail.notEmpty}")
    private String usernameOrEmail;
    @NotEmpty(message = "{login.password.notEmpty}")
    private String password;
}
