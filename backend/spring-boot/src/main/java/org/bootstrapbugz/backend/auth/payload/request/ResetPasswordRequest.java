package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.backend.shared.constants.Regex;
import org.bootstrapbugz.backend.shared.validator.FieldMatch;

@FieldMatch(first = "password", second = "confirmPassword", message = "{user.passwordsDoNotMatch}")
public record ResetPasswordRequest(
    @NotBlank(message = "{auth.tokenRequired}") String token,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String password,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String confirmPassword) {}
