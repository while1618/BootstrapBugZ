package org.bootstrapbugz.api.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RefreshTokenResponse {
  private String accessToken;
  private String refreshToken;
}
