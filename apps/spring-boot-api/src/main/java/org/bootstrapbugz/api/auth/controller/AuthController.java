package org.bootstrapbugz.api.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.bootstrapbugz.api.auth.response.RefreshTokenResponse;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(Path.AUTH)
@RestController
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<RefreshTokenResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
    return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest, request));
  }

  @PostMapping("/sign-up")
  public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.CREATED);
  }

  @GetMapping("/confirm-registration")
  public ResponseEntity<Void> confirmRegistration(@RequestParam("token") String token) {
    authService.confirmRegistration(token);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/resend-confirmation-email")
  public ResponseEntity<Void> resendConfirmationEmail(
      @Valid @RequestBody ResendConfirmationEmailRequest resendConfirmationEmailRequest) {
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    authService.forgotPassword(forgotPasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    authService.resetPassword(resetPasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader(JwtUtil.AUTH_HEADER) String token) {
    authService.logout(token);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/logout-from-all-devices")
  public ResponseEntity<Void> logoutFromAllDevices() {
    authService.logoutFromAllDevices();
    return ResponseEntity.noContent().build();
  }
}
