package org.bootstrapbugz.api.auth.controller;

import javax.validation.Valid;
import org.bootstrapbugz.api.auth.dto.RefreshTokenDto;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.dto.SimpleUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<RefreshTokenDto> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
  }

  @PostMapping("/sign-up")
  public ResponseEntity<SimpleUserDto> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
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
  public ResponseEntity<Void> logout() {
    authService.logout();
    return ResponseEntity.noContent().build();
  }
}
