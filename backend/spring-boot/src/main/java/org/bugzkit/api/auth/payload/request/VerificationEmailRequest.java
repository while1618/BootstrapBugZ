package org.bugzkit.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import org.bugzkit.api.shared.validator.UsernameOrEmail;

public record VerificationEmailRequest(
    @NotBlank(message = "{user.usernameOrEmailRequired}")
        @UsernameOrEmail(message = "{user.usernameOrEmailInvalid}")
        String usernameOrEmail) {}
