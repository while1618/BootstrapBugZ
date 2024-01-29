package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.backend.shared.constants.Regex;
import org.bootstrapbugz.backend.shared.validator.FieldMatch;

@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
public record ResetPasswordRequest(
    @NotBlank(message = "{token.required}") String token,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String password,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmPassword) {}
