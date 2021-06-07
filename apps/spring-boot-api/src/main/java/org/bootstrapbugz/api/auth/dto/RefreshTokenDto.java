package org.bootstrapbugz.api.auth.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshTokenDto {
  private String token;
  private String refreshToken;
}
