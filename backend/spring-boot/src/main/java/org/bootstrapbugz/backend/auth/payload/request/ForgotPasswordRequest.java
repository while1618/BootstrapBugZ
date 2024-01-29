package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.bootstrapbugz.backend.shared.constants.Regex;

public record ForgotPasswordRequest(
    @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email) {}
