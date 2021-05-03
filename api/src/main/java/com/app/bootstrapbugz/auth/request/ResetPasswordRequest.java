package com.app.bootstrapbugz.auth.request;

import com.app.bootstrapbugz.shared.constants.Regex;
import com.app.bootstrapbugz.shared.validator.FieldMatch;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public class ResetPasswordRequest {
  private String token;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String password;

  private String confirmPassword;
}
