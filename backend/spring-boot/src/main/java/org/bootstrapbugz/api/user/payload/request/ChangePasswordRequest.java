package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public record ChangePasswordRequest(
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String oldPassword,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String newPassword,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String confirmNewPassword) {}
