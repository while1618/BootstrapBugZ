package com.app.bootstrapbugz.dto.request.auth;

import lombok.*;

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
