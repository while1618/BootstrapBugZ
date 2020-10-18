package com.app.bootstrapbugz.auth;

import com.app.bootstrapbugz.auth.dto.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.dto.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.SignUpRequest;
import com.app.bootstrapbugz.user.dto.model.UserDto;
import com.app.bootstrapbugz.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> confirmRegistration(@RequestParam("token") String token) {
        authService.confirmRegistration(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<Void> resendConfirmationEmail(@Valid @RequestBody ResendConfirmationEmailRequest resendConfirmationEmailRequest) {
        authService.resendConfirmationEmail(resendConfirmationEmailRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
