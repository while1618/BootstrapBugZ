package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;

@Builder
public record PatchProfileRequest(
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}") String firstName,
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}") String lastName,
    @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String username,
    @Email(message = "{email.invalid}", regexp = Regex.EMAIL) String email) {}
