package com.app.bootstrapbugz.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmationEmailRequest {
    @NotEmpty(message = "{username.notEmpty}")
    private String username;
}
