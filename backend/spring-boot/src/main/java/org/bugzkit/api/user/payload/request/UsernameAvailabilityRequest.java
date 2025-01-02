package org.bugzkit.api.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bugzkit.api.shared.constants.Regex;

public record UsernameAvailabilityRequest(
    @NotBlank(message = "{user.usernameRequired}")
        @Pattern(regexp = Regex.USERNAME, message = "{user.usernameInvalid}")
        String username) {}
