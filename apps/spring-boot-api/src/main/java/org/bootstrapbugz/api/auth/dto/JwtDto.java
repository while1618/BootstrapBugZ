package org.bootstrapbugz.api.auth.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.user.dto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class JwtDto {
  private String token;
  private String refreshToken;
  private UserDto user;
}
