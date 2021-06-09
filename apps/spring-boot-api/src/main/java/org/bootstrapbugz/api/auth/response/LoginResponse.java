package org.bootstrapbugz.api.auth.response;

import org.bootstrapbugz.api.user.response.UserResponse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class LoginResponse {
  private String token;
  private String refreshToken;
  private UserResponse user;
}
