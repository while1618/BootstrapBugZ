package com.app.bootstrapbugz.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "{username.notEmpty}")
    private String username;
    @NotEmpty(message = "{password.notEmpty}")
    private String password;
}
