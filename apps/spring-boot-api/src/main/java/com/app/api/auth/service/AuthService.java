package com.app.api.auth.service;

import com.app.api.auth.request.ForgotPasswordRequest;
import com.app.api.auth.request.ResendConfirmationEmailRequest;
import com.app.api.auth.request.ResetPasswordRequest;
import com.app.api.auth.request.SignUpRequest;
import com.app.api.user.dto.SimpleUserDto;

public interface AuthService {
  SimpleUserDto signUp(SignUpRequest signUpRequest);

  void confirmRegistration(String token);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  void logout();
}
