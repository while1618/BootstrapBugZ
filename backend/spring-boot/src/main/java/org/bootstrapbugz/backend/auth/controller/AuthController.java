package org.bootstrapbugz.backend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Path.AUTH)
public class AuthController {
  private static final Marker MARKER = MarkerFactory.getMarker("AUTH");
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(
      @Valid @RequestBody RegisterUserRequest registerUserRequest) {
    log.warn(MARKER, "user: {} attempted registration", registerUserRequest.username());
    var userDTO = authService.register(registerUserRequest);
    log.info(MARKER, "user: {} registered", registerUserRequest.username());
    return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
  }

  @PostMapping("/tokens")
  public ResponseEntity<AuthTokensDTO> authenticate(
      @Valid @RequestBody AuthTokensRequest authTokensRequest, HttpServletRequest request) {
    log.warn(MARKER, "user: {} attempted authentication", authTokensRequest.usernameOrEmail());
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    var authTokensDTO = authService.authenticate(authTokensRequest, ipAddress);
    log.info(MARKER, "user: {} authenticated", authTokensRequest.usernameOrEmail());
    return ResponseEntity.ok(authTokensDTO);
  }

  @DeleteMapping("/tokens")
  public ResponseEntity<Void> deleteTokens(HttpServletRequest request) {
    final var accessToken = JwtUtil.removeBearer(AuthUtil.getAccessTokenFromRequest(request));
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    authService.deleteTokens(accessToken, ipAddress);
    log.info(MARKER, "user: {} signed out", AuthUtil.findSignedInUser().getUsername());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/tokens/devices")
  public ResponseEntity<Void> deleteTokensOnAllDevices() {
    authService.deleteTokensOnAllDevices();
    log.info(
        MARKER, "user: {} signed out from all devices", AuthUtil.findSignedInUser().getUsername());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/tokens/refresh")
  public ResponseEntity<AuthTokensDTO> refreshTokens(
      @Valid @RequestBody RefreshAuthTokensRequest refreshAuthTokensRequest,
      HttpServletRequest request) {
    log.warn(
        MARKER,
        "user: {} attempted token refresh",
        JwtUtil.getUserId(refreshAuthTokensRequest.refreshToken()));
    final var refreshToken = JwtUtil.removeBearer(refreshAuthTokensRequest.refreshToken());
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    var authTokensDTO = authService.refreshTokens(refreshToken, ipAddress);
    log.info(MARKER, "user: {} refreshed tokens", JwtUtil.getUserId(refreshToken));
    return ResponseEntity.ok(authTokensDTO);
  }

  @PostMapping("/password/forgot")
  public ResponseEntity<Void> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    log.warn(MARKER, "user: {} requested password reset", forgotPasswordRequest.email());
    authService.forgotPassword(forgotPasswordRequest);
    log.warn(MARKER, "password reset email sent to user: {}", forgotPasswordRequest.email());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/password/reset")
  public ResponseEntity<Void> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    log.warn(
        MARKER,
        "user: {} requested password reset",
        JwtUtil.getUserId(resetPasswordRequest.token()));
    authService.resetPassword(resetPasswordRequest);
    log.info(MARKER, "user: {} changed password", JwtUtil.getUserId(resetPasswordRequest.token()));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verification-email")
  public ResponseEntity<Void> sendVerificationMail(
      @Valid @RequestBody VerificationEmailRequest verificationEmailRequest) {
    authService.sendVerificationMail(verificationEmailRequest);
    log.info(
        MARKER, "verification email sent to user: {}", verificationEmailRequest.usernameOrEmail());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
    authService.verifyEmail(verifyEmailRequest);
    log.info(MARKER, "user: {} verified email", JwtUtil.getUserId(verifyEmailRequest.token()));
    return ResponseEntity.noContent().build();
  }
}
