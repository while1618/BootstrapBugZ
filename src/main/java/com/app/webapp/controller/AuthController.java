package com.app.webapp.controller;

import com.app.webapp.error.exception.*;
import com.app.webapp.hal.UserModelAssembler;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmail;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.payload.JwtAuthenticationResponse;
import com.app.webapp.payload.LoginRequest;
import com.app.webapp.security.JwtTokenProvider;
import com.app.webapp.service.UserService;
import com.app.webapp.service.VerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final UserModelAssembler assembler;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserService userService, ApplicationEventPublisher eventPublisher, VerificationTokenService verificationTokenService, MessageSource messageSource, UserModelAssembler assembler) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.messageSource = messageSource;
        this.assembler = assembler;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()).orElseThrow(
                () -> new LoginException(messageSource.getMessage("login.badCredentials", null, LocaleContextHolder.getLocale()))
        );
        if (!user.isActivated())
            throw new LoginException(messageSource.getMessage("login.notActivated", null, LocaleContextHolder.getLocale()));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception e) {
            throw new LoginException(messageSource.getMessage("login.badCredentials", null, LocaleContextHolder.getLocale()));
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@Valid @RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistException(messageSource.getMessage("email.exists", null, LocaleContextHolder.getLocale()));
        if (userService.existsByUsername(user.getUsername()))
            throw new UserAlreadyExistException(messageSource.getMessage("username.exists", null, LocaleContextHolder.getLocale()));
        User model = assembler.toModel(userService.save(user));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new VerificationTokenNotFoundException(messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale())));
        if (verificationToken.isUsed())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (LocalDateTime.now().isAfter(verificationToken.getExpirationDate()))
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.expired", null, LocaleContextHolder.getLocale()));
        userService.activate(verificationToken.getUser());
        verificationTokenService.deactivate(verificationToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new VerificationTokenNotFoundException(messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale())));
        if (verificationToken.isUsed())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (verificationToken.getNumberOfSent() > VerificationToken.getMaxNumberOfSent())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.maxNumberOfSent", null, LocaleContextHolder.getLocale()));
        eventPublisher.publishEvent(new OnResendVerificationEmail(verificationToken));
        return ResponseEntity.noContent().build();
    }
}
