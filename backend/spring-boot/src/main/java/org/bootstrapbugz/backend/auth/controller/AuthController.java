package org.bootstrapbugz.backend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.backend.auth.payload.dto.AuthTokensDTO;
import org.bootstrapbugz.backend.auth.payload.request.AuthTokensRequest;
import org.bootstrapbugz.backend.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.backend.auth.payload.request.RefreshAuthTokensRequest;
import org.bootstrapbugz.backend.auth.payload.request.RegisterUserRequest;
import org.bootstrapbugz.backend.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.backend.auth.payload.request.VerificationEmailRequest;
import org.bootstrapbugz.backend.auth.payload.request.VerifyEmailRequest;
import org.bootstrapbugz.backend.auth.service.AuthService;
import org.bootstrapbugz.backend.auth.util.AuthUtil;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.AUTH)
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(
      @Valid @RequestBody RegisterUserRequest registerUserRequest) {
    return new ResponseEntity<>(authService.register(registerUserRequest), HttpStatus.CREATED);
  }

  @PostMapping("/tokens")
  public ResponseEntity<AuthTokensDTO> authenticate(
      @Valid @RequestBody AuthTokensRequest authTokensRequest, HttpServletRequest request) {
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    return ResponseEntity.ok(authService.authenticate(authTokensRequest, ipAddress));
  }

  @DeleteMapping("/tokens")
  public ResponseEntity<Void> deleteTokens(HttpServletRequest request) {
    final var accessToken = JwtUtil.removeBearer(AuthUtil.getAccessTokenFromRequest(request));
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    authService.deleteTokens(accessToken, ipAddress);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/tokens/devices")
  public ResponseEntity<Void> deleteTokensOnAllDevices() {
    authService.deleteTokensOnAllDevices();
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/tokens/refresh")
  public ResponseEntity<AuthTokensDTO> refreshTokens(
      @Valid @RequestBody RefreshAuthTokensRequest refreshAuthTokensRequest,
      HttpServletRequest request) {
    final var refreshToken = JwtUtil.removeBearer(refreshAuthTokensRequest.refreshToken());
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    return ResponseEntity.ok(authService.refreshTokens(refreshToken, ipAddress));
  }

  @PostMapping("/password/forgot")
  public ResponseEntity<Void> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    authService.forgotPassword(forgotPasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/password/reset")
  public ResponseEntity<Void> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    authService.resetPassword(resetPasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verification-email")
  public ResponseEntity<Void> sendVerificationMail(
      @Valid @RequestBody VerificationEmailRequest verificationEmailRequest) {
    authService.sendVerificationMail(verificationEmailRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
    authService.verifyEmail(verifyEmailRequest);
    return ResponseEntity.noContent().build();
  }
}
