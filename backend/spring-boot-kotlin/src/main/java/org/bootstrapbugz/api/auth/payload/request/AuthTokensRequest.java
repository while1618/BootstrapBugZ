package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public record AuthTokensRequest(
    @NotBlank(message = "{usernameOrEmail.required}") @UsernameOrEmail(message = "{auth.invalid}")
        String usernameOrEmail,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{auth.invalid}")
        String password) {}
