package com.app.webapp.controller;

import com.app.webapp.dto.model.user.UserDto;
import com.app.webapp.dto.request.auth.ForgotPasswordRequest;
import com.app.webapp.dto.request.auth.ResendConfirmationEmailRequest;
import com.app.webapp.dto.request.auth.ResetPasswordRequest;
import com.app.webapp.dto.request.auth.SignUpRequest;
import com.app.webapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserDto userDto = authService.signUp(signUpRequest);
        return ResponseEntity
                .created(URI.create(userDto.getRequiredLink("self").getHref()))
                .body(userDto);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        authService.confirmRegistration(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@Valid @RequestBody ResendConfirmationEmailRequest resendConfirmationEmailRequest) {
        authService.resendConfirmationMail(resendConfirmationEmailRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
