package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.bootstrapbugz.api.shared.constants.Regex;

public record ForgotPasswordRequest(
    @NotBlank @Email(message = "{email.invalid}", regexp = Regex.EMAIL) String email) {}
