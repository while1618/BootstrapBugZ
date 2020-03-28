package com.app.webapp.controller.rest;

import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmail;
import com.app.webapp.exception.NotValidVerificationTokenException;
import com.app.webapp.exception.UserAlreadyExistException;
import com.app.webapp.exception.VerificationTokenNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.service.registration.IUserService;
import com.app.webapp.service.registration.IVerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequestMapping("/api")
@RestController
public class RestRegistrationController {
    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    public RestRegistrationController(IUserService userService, ApplicationEventPublisher eventPublisher, IVerificationTokenService verificationTokenService, MessageSource messageSource) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.messageSource = messageSource;
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@Valid @RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exist.", new UserAlreadyExistException());
        if (userService.existsByUsername(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exist.", new UserAlreadyExistException());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found", new VerificationTokenNotFoundException()));
        if (verificationToken.isUsed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used", new NotValidVerificationTokenException());
        if (LocalDateTime.now().isAfter(verificationToken.getExpirationDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired", new NotValidVerificationTokenException());
        activateUser(verificationToken.getUser());
        deactivateToken(verificationToken);
        return ResponseEntity.noContent().build();
    }

    private void activateUser(User user) {
        user.setActivated(true);
        user.setConfirmPassword(user.getPassword());
        userService.save(user);
    }

    private void deactivateToken(VerificationToken verificationToken) {
        verificationToken.setUsed(true);
        verificationTokenService.save(verificationToken);
    }

    @GetMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found", new VerificationTokenNotFoundException()));
        if (verificationToken.isUsed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used", new NotValidVerificationTokenException());
        if (verificationToken.getNumberOfSent() > VerificationToken.MAX_NUMBER_OF_SENT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can not resend confirmation email anymore", new NotValidVerificationTokenException());
        eventPublisher.publishEvent(new OnResendVerificationEmail(verificationToken));
        return ResponseEntity.noContent().build();
    }
}
