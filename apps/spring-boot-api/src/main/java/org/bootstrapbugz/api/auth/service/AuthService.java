package org.bootstrapbugz.api.auth.service;

import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.user.dto.SimpleUserDto;

public interface AuthService {
  SimpleUserDto signUp(SignUpRequest signUpRequest);

  void confirmRegistration(String token);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  void logout();
}
