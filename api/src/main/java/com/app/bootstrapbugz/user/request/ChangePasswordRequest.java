package com.app.bootstrapbugz.user.request;

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
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public class ChangePasswordRequest {
  private String oldPassword;

  @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
  private String newPassword;

  private String confirmNewPassword;
}
