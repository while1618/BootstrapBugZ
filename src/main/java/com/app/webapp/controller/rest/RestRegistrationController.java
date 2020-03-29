package com.app.webapp.controller.rest;

import com.app.webapp.assembler.AccountAssembler;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmail;
import com.app.webapp.exception.NotValidVerificationTokenException;
import com.app.webapp.exception.UserAlreadyExistException;
import com.app.webapp.exception.VerificationTokenNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.service.IUserService;
import com.app.webapp.service.registration.IVerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final AccountAssembler accountAssembler;

    public RestRegistrationController(IUserService userService, ApplicationEventPublisher eventPublisher, IVerificationTokenService verificationTokenService, MessageSource messageSource, AccountAssembler accountAssembler) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.messageSource = messageSource;
        this.accountAssembler = accountAssembler;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<EntityModel<User>> signUp(@Valid @RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("email.exists", null, LocaleContextHolder.getLocale()),
                    new UserAlreadyExistException()
            );
        if (userService.existsByUsername(user.getUsername()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("username.exists", null, LocaleContextHolder.getLocale()),
                    new UserAlreadyExistException()
            );
        //TODO: encode password
        EntityModel<User> model = accountAssembler.toModel(userService.save(user));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale()),
                        new VerificationTokenNotFoundException()
                ));
        if (verificationToken.isUsed())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()),
                    new NotValidVerificationTokenException()
            );
        if (LocalDateTime.now().isAfter(verificationToken.getExpirationDate()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("verificationToken.expired", null, LocaleContextHolder.getLocale()),
                    new NotValidVerificationTokenException()
            );
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
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale()),
                        new VerificationTokenNotFoundException()
                ));
        if (verificationToken.isUsed())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()),
                    new NotValidVerificationTokenException());
        if (verificationToken.getNumberOfSent() > VerificationToken.MAX_NUMBER_OF_SENT)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("verificationToken.maxNumberOfSent", null, LocaleContextHolder.getLocale()),
                    new NotValidVerificationTokenException()
            );
        eventPublisher.publishEvent(new OnResendVerificationEmail(verificationToken));
        return ResponseEntity.noContent().build();
    }
}
