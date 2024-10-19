package org.bootstrapbugz.backend.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.backend.shared.constants.Regex;

@Builder
public record PatchProfileRequest(
    @Pattern(regexp = Regex.USERNAME, message = "{user.usernameInvalid}") String username,
    @Email(message = "{user.emailInvalid}", regexp = Regex.EMAIL) String email) {}
