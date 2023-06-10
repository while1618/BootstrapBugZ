package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public record ChangePasswordRequest(
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String currentPassword,
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String newPassword,
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmNewPassword) {}
