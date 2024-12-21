package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshAuthTokensRequest(
    @NotBlank(message = "{auth.tokenRequired}") String refreshToken) {}
