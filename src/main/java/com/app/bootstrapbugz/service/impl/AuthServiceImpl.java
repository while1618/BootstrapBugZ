package com.app.bootstrapbugz.service.impl;

import com.app.bootstrapbugz.dto.model.user.UserDto;
import com.app.bootstrapbugz.dto.request.auth.ForgotPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.dto.request.auth.ResetPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.SignUpRequest;
import com.app.bootstrapbugz.constant.ErrorDomains;
import com.app.bootstrapbugz.error.exception.ForbiddenException;
import com.app.bootstrapbugz.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.event.OnSendConfirmationEmail;
import com.app.bootstrapbugz.event.OnSendForgotPasswordEmail;
import com.app.bootstrapbugz.hal.user.UserDtoModelAssembler;
import com.app.bootstrapbugz.model.user.Role;
import com.app.bootstrapbugz.model.user.RoleName;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.repository.user.RoleRepository;
import com.app.bootstrapbugz.repository.user.UserRepository;
import com.app.bootstrapbugz.constant.JwtProperties;
import com.app.bootstrapbugz.security.jwt.JwtUtilities;
import com.app.bootstrapbugz.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        String token = jwtUtilities.createToken(user, JwtProperties.CONFIRM_REGISTRATION);
        eventPublisher.publishEvent(new OnSendConfirmationEmail(user, token));
        return assembler.toModel(new ModelMapper().map(user, UserDto.class));
    }

    private User createUser(SignUpRequest signUpRequest) {
        User user = new User()
                .setFirstName(signUpRequest.getFirstName())
                .setLastName(signUpRequest.getLastName())
                .setUsername(signUpRequest.getUsername())
                .setEmail(signUpRequest.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElse(null);
        user.addRole(role);
        return userRepository.save(user);
    }

    @Override
    public void confirmRegistration(String token) {
        String username = jwtUtilities.getSubject(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ForbiddenException(messageSource.getMessage("authToken.invalidToken", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        jwtUtilities.checkToken(token, user, JwtProperties.CONFIRM_REGISTRATION);
        activateUser(user);
    }

    private void activateUser(User user) {
        user.setActivated(true);
        user.updateUpdatedAt();
        userRepository.save(user);
    }

    @Override
    public void resendConfirmationEmail(ResendConfirmationEmailRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        if (user.isActivated())
            throw new ForbiddenException(messageSource.getMessage("user.activated", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH);
        String token = jwtUtilities.createToken(user, JwtProperties.CONFIRM_REGISTRATION);
        eventPublisher.publishEvent(new OnSendConfirmationEmail(user, token));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        String token = jwtUtilities.createToken(user, JwtProperties.FORGOT_PASSWORD);
        eventPublisher.publishEvent(new OnSendForgotPasswordEmail(user, token));
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String username = jwtUtilities.getSubject(resetPasswordRequest.getToken());
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ForbiddenException(messageSource.getMessage("authToken.invalidToken", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        jwtUtilities.checkToken(resetPasswordRequest.getToken(), user, JwtProperties.FORGOT_PASSWORD);
        changePassword(user, resetPasswordRequest.getPassword());
    }

    private void changePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.updateUpdatedAt();
        userRepository.save(user);
    }
}
