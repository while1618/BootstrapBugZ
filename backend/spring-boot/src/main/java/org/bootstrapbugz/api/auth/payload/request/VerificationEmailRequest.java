package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotNull;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public record VerificationEmailRequest(
    @NotNull @UsernameOrEmail(message = "{usernameOrEmail.invalid}") String usernameOrEmail) {}
