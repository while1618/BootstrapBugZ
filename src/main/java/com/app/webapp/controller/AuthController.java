package com.app.webapp.controller;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.LoginRequest;
import com.app.webapp.dto.request.SignUpRequest;
import com.app.webapp.dto.response.JwtAuthenticationResponse;
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

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
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

    @GetMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        authService.resendConfirmationMail(usernameOrEmail);
        return ResponseEntity.noContent().build();
    }
}
