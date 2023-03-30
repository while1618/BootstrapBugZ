package org.bootstrapbugz.api.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bootstrapbugz.api.shared.constants.Regex;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
  @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
  private String firstName;

  @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
  private String lastName;

  @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
  private String username;

  @NotEmpty(message = "{email.invalid}")
  @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
  private String email;
}