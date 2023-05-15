package org.bootstrapbugz.api.auth.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

@Builder
public record SignInDTO(
    String accessToken, String refreshToken, @JsonProperty("user") UserDTO userDTO) {}
