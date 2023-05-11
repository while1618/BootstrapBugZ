package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
  @UsernameOrEmail(message = "{signIn.invalid}")
  private String usernameOrEmail;

  @Pattern(regexp = Regex.PASSWORD, message = "{signIn.invalid}")
  private String password;
}
