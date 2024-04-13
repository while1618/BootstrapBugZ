package org.bootstrapbugz.backend.auth.service;

import org.bootstrapbugz.backend.auth.payload.dto.AuthTokensDTO;
import org.bootstrapbugz.backend.auth.payload.request.AuthTokensRequest;
import org.bootstrapbugz.backend.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.backend.auth.payload.request.RegisterUserRequest;
import org.bootstrapbugz.backend.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.backend.auth.payload.request.VerificationEmailRequest;
import org.bootstrapbugz.backend.auth.payload.request.VerifyEmailRequest;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;

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
