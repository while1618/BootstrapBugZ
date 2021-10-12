package org.bootstrapbugz.api.auth.service;

import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.payload.response.RefreshTokenResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

public interface AuthService {
  UserResponse getLoggedInUser();

  RefreshTokenResponse refreshToken(
      RefreshTokenRequest refreshTokenRequest, HttpServletRequest request);

  UserResponse signUp(SignUpRequest signUpRequest);

  void confirmRegistration(String token);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  void logout(HttpServletRequest request);

  void logoutFromAllDevices();

  boolean isUsernameAvailable(String username);

  boolean isEmailAvailable(String email);
}
