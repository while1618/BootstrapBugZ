package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public record AuthTokensRequest(
    @NotNull @UsernameOrEmail(message = "{signIn.invalid}") String usernameOrEmail,
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{signIn.invalid}") String password) {}
