package org.bootstrapbugz.api.auth.payload.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class SignInResponse {
  private String accessToken;
  private String refreshToken;
  private UserResponse user;
}
