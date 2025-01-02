package org.bugzkit.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.bugzkit.api.shared.constants.Regex;

public record ForgotPasswordRequest(
    @NotBlank(message = "{user.emailRequired}")
        @Email(message = "{user.emailInvalid}", regexp = Regex.EMAIL)
        String email) {}
