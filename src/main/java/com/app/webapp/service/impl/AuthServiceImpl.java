package com.app.webapp.service.impl;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.LoginRequest;
import com.app.webapp.dto.request.SignUpRequest;
import com.app.webapp.dto.response.JwtAuthenticationResponse;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.LoginException;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.error.exception.VerificationTokenNotValidException;
import com.app.webapp.event.OnRegistrationCompleteEvent;
import com.app.webapp.event.OnResendVerificationEmailEvent;
import com.app.webapp.hal.UserDtoModelAssembler;
import com.app.webapp.model.Role;
import com.app.webapp.model.RoleName;
import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.RoleRepository;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.repository.VerificationTokenRepository;
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

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final PasswordEncoder bCryptPasswordEncoder;

    private final UserDtoModelAssembler assembler;
    private final JwtTokenUtilities jwtUtilities;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           VerificationTokenRepository verificationTokenRepository, AuthenticationManager authenticationManager,
                           ApplicationEventPublisher eventPublisher, MessageSource messageSource, PasswordEncoder bCryptPasswordEncoder,
                           UserDtoModelAssembler assembler, JwtTokenUtilities jwtUtilities) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
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
        User user = userRepository.save(createUser(signUpRequest));
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
        Role role = roleRepository.findByName(RoleName.USER).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("role.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.ROLE));
        user.addRole(role);

        return user;
    }

    @Override
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.VERIFICATION_TOKEN));
        if (verificationToken.isUsed())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (LocalDateTime.now().isAfter(verificationToken.getExpirationDate()))
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.expired", null, LocaleContextHolder.getLocale()));
        activateUser(verificationToken.getUser());
        deactivateToken(verificationToken);
    }

    private void activateUser(User user) {
        user.setActivated(true);
        userRepository.save(user);
    }

    private void deactivateToken(VerificationToken token) {
        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    @Override
    public void resendConfirmationMail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.USER));
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("verificationToken.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.VERIFICATION_TOKEN));
        if (verificationToken.isUsed())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.used", null, LocaleContextHolder.getLocale()));
        if (verificationToken.getNumberOfSent() > VerificationToken.getMaxNumberOfSent())
            throw new VerificationTokenNotValidException(messageSource.getMessage("verificationToken.maxNumberOfSent", null, LocaleContextHolder.getLocale()));
        eventPublisher.publishEvent(new OnResendVerificationEmailEvent(verificationToken));
    }
}
