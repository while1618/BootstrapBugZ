package org.bootstrapbugz.api.auth.payload.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.user.payload.dto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class SignInDto {
  private String accessToken;
  private String refreshToken;
  private UserDto user;
}
