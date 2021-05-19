package org.bootstrapbugz.api.user.request;

import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public class ChangePasswordRequest {
  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String oldPassword;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String newPassword;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String confirmNewPassword;
}
