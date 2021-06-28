package org.bootstrapbugz.api.auth.service.impl;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;

import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.response.RefreshTokenResponse;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.response.UserResponse;
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
  public RefreshTokenResponse refreshToken(
      RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
    final String refreshToken =
        JwtUtil.removeTokenTypeFromToken(refreshTokenRequest.getRefreshToken());
    jwtService.checkRefreshToken(refreshToken);
    jwtService.deleteRefreshToken(refreshToken);
    final String username = JWT.decode(refreshToken).getSubject();
    final String accessToken = jwtService.createToken(username, JwtPurpose.ACCESSING_RESOURCES);
    final String newRefreshToken =
        jwtService.createRefreshToken(username, AuthUtil.getUserIpAddress(request));
    return new RefreshTokenResponse(accessToken, newRefreshToken);
  }

  @Override
  public UserResponse signUp(SignUpRequest signUpRequest) {
    final var user = createUser(signUpRequest);
    final String token =
        jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
    return userMapper.userToUserResponse(user);
  }

  private User createUser(SignUpRequest signUpRequest) {
    final var user =
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
    final String username = JWT.decode(token).getSubject();
    final var user =
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
    final var user =
        userRepository
            .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    if (user.isActivated())
      throw new ForbiddenException(messageService.getMessage("user.activated"), ErrorDomain.AUTH);
    final String token =
        jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
  }

  @Override
  public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    final var user =
        userRepository
            .findByEmail(forgotPasswordRequest.getEmail())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    final String token = jwtService.createToken(user.getUsername(), JwtPurpose.FORGOT_PASSWORD);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.FORGOT_PASSWORD));
  }

  @Override
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    final String username = JWT.decode(resetPasswordRequest.getToken()).getSubject();
    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ForbiddenException(
                        messageService.getMessage("token.invalid"), ErrorDomain.AUTH));
    jwtService.checkToken(resetPasswordRequest.getToken(), JwtPurpose.FORGOT_PASSWORD);
    user.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
    jwtService.invalidateAllTokens(user.getUsername());
    jwtService.deleteAllRefreshTokensByUser(user.getUsername());
    userRepository.save(user);
  }

  @Override
  public void logout(HttpServletRequest request) {
    jwtService.deleteRefreshTokenByUserAndIpAddress(
        AuthUtil.findLoggedUser().getUsername(), AuthUtil.getUserIpAddress(request));
    jwtService.invalidateToken(
        JwtUtil.removeTokenTypeFromToken(AuthUtil.getAuthTokenFromRequest(request)));
  }

  @Override
  public void logoutFromAllDevices() {
    final String username = AuthUtil.findLoggedUser().getUsername();
    jwtService.deleteAllRefreshTokensByUser(username);
    jwtService.invalidateAllTokens(username);
  }
}
