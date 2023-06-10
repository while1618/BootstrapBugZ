package org.bootstrapbugz.api.auth.payload.dto;

import lombok.Builder;

@Builder
public record AuthTokensDTO(String accessToken, String refreshToken) {}
