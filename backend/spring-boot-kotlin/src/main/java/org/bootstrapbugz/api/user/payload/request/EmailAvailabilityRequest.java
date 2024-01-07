package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.bootstrapbugz.api.shared.constants.Regex;

public record EmailAvailabilityRequest(
    @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email) {}
