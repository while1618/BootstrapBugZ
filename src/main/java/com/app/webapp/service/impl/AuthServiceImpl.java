package com.app.webapp.service.impl;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.*;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.BadRequestException;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.event.OnForgotPasswordEvent;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.hal.UserDtoModelAssembler;
import com.app.webapp.model.*;
import com.app.webapp.repository.RoleRepository;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.security.JwtProperties;
import com.app.webapp.security.JwtUtilities;
import com.app.webapp.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtilities jwtUtilities;

    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final PasswordEncoder bCryptPasswordEncoder;

    private final UserDtoModelAssembler assembler;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           JwtUtilities jwtUtilities, ApplicationEventPublisher eventPublisher,
                           MessageSource messageSource, PasswordEncoder bCryptPasswordEncoder,
                           UserDtoModelAssembler assembler) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtilities = jwtUtilities;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.assembler = assembler;
    }

    @Override
    public UserDto signUp(SignUpRequest signUpRequest) {
        User user = createUser(signUpRequest);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return assembler.toModel(new ModelMapper().map(user, UserDto.class));
    }

    private User createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElse(null);
        user.addRole(role);
        return userRepository.save(user);
    }

    @Override
    public void confirmRegistration(String token) {
        String username = jwtUtilities.getSubject(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        jwtUtilities.checkToken(token, user, JwtProperties.CONFIRM_REGISTRATION);
        activateUser(user);
    }

    private void activateUser(User user) {
        user.setActivated(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void resendConfirmationMail(ResendConfirmationEmailRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        if (user.isActivated())
            throw new BadRequestException(messageSource.getMessage("user.activated", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH);
        eventPublisher.publishEvent(new OnResendVerificationEmailEvent(user));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        eventPublisher.publishEvent(new OnForgotPasswordEvent(user));
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String username = jwtUtilities.getSubject(resetPasswordRequest.getToken());
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        jwtUtilities.checkToken(resetPasswordRequest.getToken(), user, JwtProperties.FORGOT_PASSWORD);
        changePassword(user, resetPasswordRequest.getPassword());
    }

    private void changePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
