package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import org.bootstrapbugz.backend.shared.validator.UsernameOrEmail;

public record VerificationEmailRequest(
    @NotBlank(message = "{usernameOrEmail.required}")
        @UsernameOrEmail(message = "{usernameOrEmail.invalid}")
        String usernameOrEmail) {}
