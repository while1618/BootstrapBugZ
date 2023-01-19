package org.bootstrapbugz.api.auth.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class SignInDTO {
  private String accessToken;
  private String refreshToken;

  @JsonProperty("user")
  private UserDTO userDTO;
}
