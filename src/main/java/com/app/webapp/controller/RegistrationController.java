package com.app.webapp.controller;

import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmail;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.service.IUserService;
import com.app.webapp.service.registration.IVerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class RegistrationController {
    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    public RegistrationController(IUserService userService, ApplicationEventPublisher eventPublisher, IVerificationTokenService verificationTokenService, MessageSource messageSource) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("registration/login");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @GetMapping("/sign-up")
    public ModelAndView showSignUpForm() {
        ModelAndView modelAndView = new ModelAndView("registration/sign_up");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/sign-up")
    public ModelAndView signUp(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (userService.existsByEmail(user.getEmail()))
            bindingResult.rejectValue("email", "exist", messageSource.getMessage("Exist.email", null, LocaleContextHolder.getLocale()));
        if (userService.existsByUsername(user.getUsername()))
            bindingResult.rejectValue("username", "exist", messageSource.getMessage("Exist.username", null, LocaleContextHolder.getLocale()));
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration/sign_up");
        } else {
            userService.save(user);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
            modelAndView.setViewName("registration/activate_account");
        }
        return modelAndView;
    }

    @GetMapping("/confirm-registration")
    public ModelAndView confirmRegistration(@RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<VerificationToken> verificationToken = verificationTokenService.optionalFindByToken(token);
        if (verificationToken.isPresent()) {
            if (!verificationToken.get().isUsed()) {
                User user = verificationToken.get().getUser();
                if (LocalDateTime.now().isBefore(verificationToken.get().getExpirationDate())) {
                    user.setActivated(true);
                    user.setConfirmPassword(user.getPassword());
                    userService.save(user);
                    verificationToken.get().setUsed(true);
                    verificationTokenService.save(verificationToken.get());
                    modelAndView.setViewName("redirect:/login");
                } else {
                    modelAndView.addObject("tokenExpired", messageSource.getMessage("Token.expired", null, LocaleContextHolder.getLocale()));
                    modelAndView.addObject("token", verificationToken.get().getToken());
                    modelAndView.setViewName("registration/bad_user");
                }
            } else {
                modelAndView.addObject("tokenUsed", messageSource.getMessage("Token.used", null, LocaleContextHolder.getLocale()));
                modelAndView.setViewName("registration/bad_user");
            }
        } else {
            modelAndView.addObject("invalidToken", messageSource.getMessage("Token.invalid", null, LocaleContextHolder.getLocale()));
            modelAndView.setViewName("registration/bad_user");
        }
        return modelAndView;
    }

    @GetMapping("/resend-confirmation-email")
    public ModelAndView resendConfirmationEmail(@RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<VerificationToken> verificationToken = verificationTokenService.optionalFindByToken(token);
        if (verificationToken.isPresent()) {
            if (!verificationToken.get().isUsed()) {
                if (verificationToken.get().getNumberOfSent() < VerificationToken.MAX_NUMBER_OF_SENT) {
                    eventPublisher.publishEvent(new OnResendVerificationEmail(verificationToken.get()));
                    modelAndView.setViewName("registration/activate_account");
                } else {
                    modelAndView.addObject("maxNumberOfSent", messageSource.getMessage("Token.maxNumberOfSent", null, LocaleContextHolder.getLocale()));
                    modelAndView.setViewName("registration/bad_user");
                }
            } else {
                modelAndView.addObject("tokenUsed", messageSource.getMessage("Token.used", null, LocaleContextHolder.getLocale()));
                modelAndView.setViewName("registration/bad_user");
            }
        } else {
            modelAndView.addObject("invalidToken", messageSource.getMessage("Token.invalid", null, LocaleContextHolder.getLocale()));
            modelAndView.setViewName("registration/bad_user");
        }
        return modelAndView;
    }
}
