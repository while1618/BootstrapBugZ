package org.bootstrapbugz.api.auth.jwt.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtilities;
import org.bootstrapbugz.api.user.dto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class JwtDto {
  private String token;
  private String tokenType = JwtUtilities.BEARER;
  private String refreshToken;
  private UserDto user;
}
