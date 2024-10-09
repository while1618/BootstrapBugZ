package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.backend.shared.constants.Regex;
import org.bootstrapbugz.backend.shared.validator.UsernameOrEmail;

public record AuthTokensRequest(
    @NotBlank(message = "{user.usernameOrEmailRequired}")
        @UsernameOrEmail(message = "{user.usernameOrEmailInvalid}")
        String usernameOrEmail,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String password) {}
