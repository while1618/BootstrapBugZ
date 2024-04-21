package org.bootstrapbugz.api.auth.service;

import org.bootstrapbugz.api.auth.payload.dto.AuthTokensDTO;
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RegisterUserRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.VerificationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.VerifyEmailRequest;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

public interface AuthService {
  UserDTO register(RegisterUserRequest registerUserRequest);

  AuthTokensDTO authenticate(AuthTokensRequest authTokensRequest, String ipAddress);

  void deleteTokens(String accessToken, String ipAddress);

  void deleteTokensOnAllDevices();

  AuthTokensDTO refreshTokens(String refreshToken, String ipAddress);

  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  void sendVerificationMail(VerificationEmailRequest verificationEmailRequest);

  void verifyEmail(VerifyEmailRequest verifyEmailRequest);
}
