package org.bootstrapbugz.api.auth.service.impl;

import com.auth0.jwt.JWT;
import java.util.Collections;
import org.bootstrapbugz.api.auth.dto.RefreshTokenDto;
import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.dto.UserDto;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final ApplicationEventPublisher eventPublisher;
  private final MessageService messageService;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final UserMapper userMapper;

  public AuthServiceImpl(
      UserRepository userRepository,
      JwtService jwtService,
      ApplicationEventPublisher eventPublisher,
      MessageService messageService,
      PasswordEncoder bCryptPasswordEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.eventPublisher = eventPublisher;
    this.messageService = messageService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userMapper = userMapper;
  }

  @Override
  public RefreshTokenDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return null;
  }

  @Override
  public UserDto signUp(SignUpRequest signUpRequest) {
    User user = createUser(signUpRequest);
    String token = jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
    return userMapper.userToUserDto(user);
  }

  private User createUser(SignUpRequest signUpRequest) {
    User user =
        new User()
            .setFirstName(signUpRequest.getFirstName())
            .setLastName(signUpRequest.getLastName())
            .setUsername(signUpRequest.getUsername())
            .setEmail(signUpRequest.getEmail())
            .setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
            .setRoles(Collections.singleton(new Role(RoleName.USER)));
    return userRepository.save(user);
  }

  @Override
  public void confirmRegistration(String token) {
    String username = JWT.decode(token).getSubject();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ForbiddenException(
                        messageService.getMessage("token.invalid"), ErrorDomain.AUTH));
    if (user.isActivated())
      throw new ForbiddenException(messageService.getMessage("user.activated"), ErrorDomain.AUTH);
    jwtService.checkToken(token, JwtPurpose.CONFIRM_REGISTRATION);
    user.setActivated(true);
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
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    if (user.isActivated())
      throw new ForbiddenException(messageService.getMessage("user.activated"), ErrorDomain.AUTH);
    String token = jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
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
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    String token = jwtService.createToken(user.getUsername(), JwtPurpose.FORGOT_PASSWORD);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.FORGOT_PASSWORD));
  }

  @Override
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    String username = JWT.decode(resetPasswordRequest.getToken()).getSubject();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ForbiddenException(
                        messageService.getMessage("token.invalid"), ErrorDomain.AUTH));
    jwtService.checkToken(resetPasswordRequest.getToken(), JwtPurpose.FORGOT_PASSWORD);
    user.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
    jwtService.invalidateAllTokens(user.getUsername());
    userRepository.save(user);
  }

  @Override
  public void logout(String token) {
    jwtService.invalidateToken(JwtUtil.removeTokenTypeFromToken(token));
  }

  @Override
  public void logoutFromAllDevices() {
    jwtService.invalidateAllTokens(AuthUtil.findLoggedUser().getUsername());
  }
}
