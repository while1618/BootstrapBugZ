package org.bootstrapbugz.api.auth.request;

import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

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
