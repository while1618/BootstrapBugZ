package com.app.webapp.controller.rest;

import com.app.webapp.hal.UserModelAssembler;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmail;
import com.app.webapp.error.exception.NotValidVerificationTokenException;
import com.app.webapp.error.exception.UserAlreadyExistException;
import com.app.webapp.error.exception.VerificationTokenNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.service.IUserService;
import com.app.webapp.service.registration.IVerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;

@RequestMapping("/api")
@RestController
public class RestRegistrationController {
    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final UserModelAssembler assembler;

    public RestRegistrationController(IUserService userService, ApplicationEventPublisher eventPublisher, IVerificationTokenService verificationTokenService, MessageSource messageSource, UserModelAssembler assembler) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.messageSource = messageSource;
        this.assembler = assembler;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@Valid @RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistException(messageSource.getMessage("email.exists", null, LocaleContextHolder.getLocale()));
        if (userService.existsByUsername(user.getUsername()))
            throw new UserAlreadyExistException(messageSource.getMessage("username.exists", null, LocaleContextHolder.getLocale()));
        //TODO: encode password
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
            throw new NotValidVerificationTokenException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (LocalDateTime.now().isAfter(verificationToken.getExpirationDate()))
            throw new NotValidVerificationTokenException(messageSource.getMessage("verificationToken.expired", null, LocaleContextHolder.getLocale()));
        activateUser(verificationToken.getUser());
        deactivateToken(verificationToken);
        return ResponseEntity.noContent().build();
    }

    private void activateUser(User user) {
        user.setActivated(true);
        userService.save(user);
    }

    private void deactivateToken(VerificationToken verificationToken) {
        verificationToken.setUsed(true);
        verificationTokenService.save(verificationToken);
    }

    @GetMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new VerificationTokenNotFoundException(messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale())));
        if (verificationToken.isUsed())
            throw new NotValidVerificationTokenException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (verificationToken.getNumberOfSent() > VerificationToken.MAX_NUMBER_OF_SENT)
            throw new NotValidVerificationTokenException(messageSource.getMessage("verificationToken.maxNumberOfSent", null, LocaleContextHolder.getLocale()));
        eventPublisher.publishEvent(new OnResendVerificationEmail(verificationToken));
        return ResponseEntity.noContent().build();
    }
}
