package com.app.webapp.service.impl;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.*;
import com.app.webapp.dto.response.JwtAuthenticationResponse;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.BadRequestException;
import com.app.webapp.error.exception.LoginException;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.error.exception.AuthTokenNotValidException;
import com.app.webapp.event.OnForgotPasswordEvent;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.hal.UserDtoModelAssembler;
import com.app.webapp.model.*;
import com.app.webapp.repository.RoleRepository;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.repository.AuthTokenRepository;
import com.app.webapp.security.JwtTokenUtilities;
import com.app.webapp.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final PasswordEncoder bCryptPasswordEncoder;

    private final UserDtoModelAssembler assembler;
    private final JwtTokenUtilities jwtUtilities;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           AuthTokenRepository authTokenRepository, AuthenticationManager authenticationManager,
                           ApplicationEventPublisher eventPublisher, MessageSource messageSource, PasswordEncoder bCryptPasswordEncoder,
                           UserDtoModelAssembler assembler, JwtTokenUtilities jwtUtilities) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenRepository = authTokenRepository;
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.assembler = assembler;
        this.jwtUtilities = jwtUtilities;
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()).orElseThrow(
                () -> new LoginException(messageSource.getMessage("login.badCredentials", null, LocaleContextHolder.getLocale())));
        if (!user.isActivated())
            throw new LoginException(messageSource.getMessage("login.notActivated", null, LocaleContextHolder.getLocale()));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new JwtAuthenticationResponse(jwtUtilities.generateToken(authentication));
        } catch (Exception e) {
            throw new LoginException(messageSource.getMessage("login.badCredentials", null, LocaleContextHolder.getLocale()));
        }
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
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("role.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        user.addRole(role);
        return userRepository.save(user);
    }

    @Override
    public void confirmRegistration(String token) {
        AuthToken authToken = authTokenRepository.findByTokenAndUsage(token, AuthTokenProperties.VERIFICATION).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("token.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        if (authToken.isExpired())
            throw new AuthTokenNotValidException(messageSource.getMessage("token.expired", null, LocaleContextHolder.getLocale()));
        activateUser(authToken.getUser());
        authTokenRepository.delete(authToken);
    }

    private void activateUser(User user) {
        user.setActivated(true);
        userRepository.save(user);
    }

    @Override
    public void resendConfirmationMail(ResendConfirmationEmailRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        if (user.isActivated())
            throw new BadRequestException(messageSource.getMessage("user.activated", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH);
        AuthToken authToken = authTokenRepository.findByUserAndUsage(user, AuthTokenProperties.VERIFICATION).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("token.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        eventPublisher.publishEvent(new OnResendVerificationEmailEvent(authToken));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        eventPublisher.publishEvent(new OnForgotPasswordEvent(user));
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        AuthToken authToken = authTokenRepository.findByTokenAndUsage(resetPasswordRequest.getToken(), AuthTokenProperties.FORGOT_PASSWORD).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("token.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.AUTH));
        if (authToken.isExpired())
            throw new AuthTokenNotValidException(messageSource.getMessage("token.expired", null, LocaleContextHolder.getLocale()));
        changePassword(authToken.getUser(), resetPasswordRequest.getPassword());
        authTokenRepository.delete(authToken);
    }

    private void changePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }
}