package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public record AuthTokensRequest(
    @NotBlank(message = "{user.usernameOrEmailRequired}")
        @UsernameOrEmail(message = "{user.usernameOrEmailInvalid}")
        String usernameOrEmail,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String password) {}
