package org.bugzkit.api.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bugzkit.api.shared.constants.Regex;
import org.bugzkit.api.shared.validator.FieldMatch;

@FieldMatch(
    first = "newPassword",
    second = "confirmNewPassword",
    message = "{user.passwordsDoNotMatch}")
public record ChangePasswordRequest(
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String currentPassword,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String newPassword,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String confirmNewPassword) {}
