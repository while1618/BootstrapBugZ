package org.bootstrapbugz.backend.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.backend.shared.constants.Regex;

public record UsernameAvailabilityRequest(
    @NotBlank(message = "{username.required}")
        @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
        String username) {}
