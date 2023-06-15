package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public record ChangePasswordRequest(
    @NotBlank @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String currentPassword,
    @NotBlank @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String newPassword,
    @NotBlank @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmNewPassword) {}
