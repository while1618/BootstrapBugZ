package org.bootstrapbugz.api.auth.payload.dto;

import lombok.Builder;

@Builder
public record RefreshTokenDTO(String accessToken, String refreshToken) {}
