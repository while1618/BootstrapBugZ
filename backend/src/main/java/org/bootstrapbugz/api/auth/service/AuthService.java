package org.bootstrapbugz.api.auth.service;

import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.payload.dto.RefreshTokenDto;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.user.payload.dto.UserDto;

public interface AuthService {
  UserDto signUp(SignUpRequest signUpRequest);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest);

  RefreshTokenDto refreshToken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest request);

  void signOut(HttpServletRequest request);

  void signOutFromAllDevices();

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  UserDto signedInUser();

  boolean isUsernameAvailable(String username);

  boolean isEmailAvailable(String email);
}
