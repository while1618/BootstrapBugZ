package com.app.bootstrapbugz.auth.controller;

import com.app.bootstrapbugz.auth.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.request.SignUpRequest;
import com.app.bootstrapbugz.auth.service.AuthService;
import com.app.bootstrapbugz.common.constants.Path;
import com.app.bootstrapbugz.user.dto.SimpleUserDto;
import javax.validation.Valid;
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
