package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokensRequest(@NotBlank(message = "{token.invalid}") String refreshToken) {}
