package org.bootstrapbugz.api.auth.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

@Builder
public record SignInDTO(
    String accessToken,
    String refreshToken,
    @JsonProperty("user") @SerializedName("user") UserDTO userDTO) {}
