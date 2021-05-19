package org.bootstrapbugz.api.auth.request;

import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  @UsernameOrEmail(message = "{login.invalid}")
  private String usernameOrEmail;

  @Pattern(regexp = Regex.PASSWORD, message = "{login.invalid}")
  private String password;
}
