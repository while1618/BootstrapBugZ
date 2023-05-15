package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.bootstrapbugz.api.shared.constants.Regex;

public record ForgotPasswordRequest(
    @NotEmpty(message = "{email.invalid}") @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email) {}
