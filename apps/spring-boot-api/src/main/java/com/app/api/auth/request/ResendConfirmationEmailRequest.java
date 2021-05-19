package com.app.api.auth.request;

import com.app.api.shared.validator.UsernameOrEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmationEmailRequest {
  @UsernameOrEmail(message = "{usernameOrEmail.invalid}")
  private String usernameOrEmail;
}
