package com.app.bootstrapbugz.auth.request;

import com.app.bootstrapbugz.shared.constants.Regex;
import com.app.bootstrapbugz.shared.validator.UsernameOrEmail;
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
