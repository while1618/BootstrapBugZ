package org.bootstrapbugz.api.auth.payload.request;

import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public record VerificationEmailRequest(
    @UsernameOrEmail(message = "{usernameOrEmail.invalid}") String usernameOrEmail) {}
