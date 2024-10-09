package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import org.bootstrapbugz.backend.shared.validator.UsernameOrEmail;

public record VerificationEmailRequest(
    @NotBlank(message = "{user.usernameOrEmailRequired}")
        @UsernameOrEmail(message = "{user.usernameOrEmailInvalid}")
        String usernameOrEmail) {}
