package org.bootstrapbugz.api.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.bootstrapbugz.api.auth.payload.dto.RefreshTokenDto;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
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

  @PostMapping("/sign-up")
  public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.CREATED);
  }

  @PostMapping("/resend-confirmation-email")
  public ResponseEntity<Void> resendConfirmationEmail(
      @Valid @RequestBody ResendConfirmationEmailRequest resendConfirmationEmailRequest) {
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/confirm-registration")
  public ResponseEntity<Void> confirmRegistration(
      @Valid @RequestBody ConfirmRegistrationRequest confirmRegistrationRequest) {
    authService.confirmRegistration(confirmRegistrationRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<RefreshTokenDto> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
    return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest, request));
  }

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(HttpServletRequest request) {
    authService.signOut(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/sign-out-from-all-devices")
  public ResponseEntity<Void> signOutFromAllDevices() {
    authService.signOutFromAllDevices();
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

  @GetMapping("/signed-in-user")
  public ResponseEntity<UserDto> signedInUser() {
    return ResponseEntity.ok(authService.signedInUser());
  }

  @GetMapping("/username-availability")
  public ResponseEntity<Boolean> isUsernameAvailable(@RequestParam("username") String username) {
    return ResponseEntity.ok(authService.isUsernameAvailable(username));
  }

  @GetMapping("/email-availability")
  public ResponseEntity<Boolean> isEmailAvailable(@RequestParam("email") String email) {
    return ResponseEntity.ok(authService.isEmailAvailable(email));
  }
}
