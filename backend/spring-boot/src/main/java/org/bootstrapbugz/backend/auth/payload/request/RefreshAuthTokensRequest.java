package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshAuthTokensRequest(
    @NotBlank(message = "{token.required}") String refreshToken) {}
