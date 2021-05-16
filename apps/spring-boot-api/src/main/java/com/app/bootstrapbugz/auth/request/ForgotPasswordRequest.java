package com.app.bootstrapbugz.auth.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
  @NotEmpty(message = "{email.invalid}")
  @Email(message = "{email.invalid}")
  private String email;
}
