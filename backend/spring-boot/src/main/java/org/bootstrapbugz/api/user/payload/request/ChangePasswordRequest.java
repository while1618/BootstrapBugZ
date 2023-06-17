package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@FieldMatch(
    first = "newPassword",
    second = "confirmNewPassword",
    message = "{passwords.doNotMatch}")
public record ChangePasswordRequest(
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String currentPassword,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String newPassword,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmNewPassword) {}
