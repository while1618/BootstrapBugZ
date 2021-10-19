package org.bootstrapbugz.api.auth.service;

import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.payload.response.RefreshTokenResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
  UserResponse signUp(SignUpRequest signUpRequest);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest);

  RefreshTokenResponse refreshToken(
      RefreshTokenRequest refreshTokenRequest, HttpServletRequest request);

  void signOut(HttpServletRequest request);

  void signOutFromAllDevices();

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  UserResponse signedInUser();

  boolean isUsernameAvailable(String username);

  boolean isEmailAvailable(String email);
}
