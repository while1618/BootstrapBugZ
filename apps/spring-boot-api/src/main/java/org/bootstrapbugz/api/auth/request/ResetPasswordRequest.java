package org.bootstrapbugz.api.auth.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.auth.validator.FieldMatch;

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
  @NotBlank(message = "{token.invalid}")
  private String token;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String password;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String confirmPassword;
}
