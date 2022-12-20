package org.bootstrapbugz.api.auth.service;

import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.payload.dto.RefreshTokenDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

public interface AuthService {
  UserDTO signUp(SignUpRequest signUpRequest);

  void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

  void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest);

  RefreshTokenDTO refreshToken(
      RefreshTokenRequest refreshTokenRequest, HttpServletRequest request);

  void signOut(HttpServletRequest request);

  void signOutFromAllDevices();

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  UserDTO signedInUser();

  boolean isUsernameAvailable(String username);

  boolean isEmailAvailable(String email);
}
