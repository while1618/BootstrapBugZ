package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(@NotBlank(message = "{token.invalid}") String token) {}