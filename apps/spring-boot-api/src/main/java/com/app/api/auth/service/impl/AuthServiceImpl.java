package com.app.api.auth.service.impl;

import com.app.api.auth.request.ForgotPasswordRequest;
import com.app.api.auth.request.ResendConfirmationEmailRequest;
import com.app.api.auth.request.ResetPasswordRequest;
import com.app.api.auth.request.SignUpRequest;
import com.app.api.auth.service.AuthService;
import com.app.api.jwt.event.OnSendJwtEmail;
import com.app.api.jwt.util.JwtPurpose;
import com.app.api.jwt.util.JwtUtilities;
import com.app.api.shared.error.ErrorDomain;
import com.app.api.shared.error.exception.ForbiddenException;
import com.app.api.shared.error.exception.ResourceNotFound;
import com.app.api.user.dto.SimpleUserDto;
import com.app.api.user.mapper.UserMapper;
import com.app.api.user.model.Role;
import com.app.api.user.model.RoleName;
import com.app.api.user.model.User;
import com.app.api.user.repository.RoleRepository;
import com.app.api.user.repository.UserRepository;
import com.app.api.user.util.UserUtilities;
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
  private final UserMapper userMapper;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      JwtUtilities jwtUtilities,
      ApplicationEventPublisher eventPublisher,
      MessageSource messageSource,
      PasswordEncoder bCryptPasswordEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.jwtUtilities = jwtUtilities;
    this.eventPublisher = eventPublisher;
    this.messageSource = messageSource;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userMapper = userMapper;
  }

  @Override
  public SimpleUserDto signUp(SignUpRequest signUpRequest) {
    User user = createUser(signUpRequest);
    String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
    return userMapper.userToSimpleUserDto(user);
  }

  private User createUser(SignUpRequest signUpRequest) {
    User user =
        new User()
            .setFirstName(signUpRequest.getFirstName())
            .setLastName(signUpRequest.getLastName())
            .setUsername(signUpRequest.getUsername())
            .setEmail(signUpRequest.getEmail())
            .setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
    Role role = roleRepository.findByName(RoleName.USER).orElse(null);
    user.addRole(role);
    return userRepository.save(user);
  }

  @Override
  public void confirmRegistration(String token) {
    String username = jwtUtilities.getSubject(token);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ForbiddenException(
                        messageSource.getMessage(
                            "authToken.invalid", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.AUTH));
    jwtUtilities.checkToken(token, user, JwtPurpose.CONFIRM_REGISTRATION);
    activateUser(user);
  }

  private void activateUser(User user) {
    user.setActivated(true);
    user.updateUpdatedAt();
    userRepository.save(user);
  }

  @Override
  public void resendConfirmationEmail(ResendConfirmationEmailRequest request) {
    User user =
        userRepository
            .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
            .orElseThrow(
                () ->
                    new ResourceNotFound(
                        messageSource.getMessage(
                            "user.notFound", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.AUTH));
    if (user.isActivated())
      throw new ForbiddenException(
          messageSource.getMessage("user.activated", null, LocaleContextHolder.getLocale()),
          ErrorDomain.AUTH);
    String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
  }

  @Override
  public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    User user =
        userRepository
            .findByEmail(forgotPasswordRequest.getEmail())
            .orElseThrow(
                () ->
                    new ResourceNotFound(
                        messageSource.getMessage(
                            "user.notFound", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.AUTH));
    String token = jwtUtilities.createToken(user, JwtPurpose.FORGOT_PASSWORD);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.FORGOT_PASSWORD));
  }

  @Override
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    String username = jwtUtilities.getSubject(resetPasswordRequest.getToken());
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ForbiddenException(
                        messageSource.getMessage(
                            "authToken.invalid", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.AUTH));
    jwtUtilities.checkToken(resetPasswordRequest.getToken(), user, JwtPurpose.FORGOT_PASSWORD);
    changePassword(user, resetPasswordRequest.getPassword());
  }

  private void changePassword(User user, String password) {
    user.setPassword(bCryptPasswordEncoder.encode(password));
    user.updateUpdatedAt();
    userRepository.save(user);
  }

  @Override
  public void logout() {
    User user = UserUtilities.findLoggedUser(userRepository, messageSource);
    user.updateLastLogout();
    userRepository.save(user);
  }
}
