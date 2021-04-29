package com.app.bootstrapbugz.auth.service;

import com.app.bootstrapbugz.auth.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.request.SignUpRequest;
import com.app.bootstrapbugz.user.dto.SimpleUserDto;

public interface AuthService {
  SimpleUserDto signUp(SignUpRequest signUpRequest);

  void confirmRegistration(String token);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  void logout();
}
