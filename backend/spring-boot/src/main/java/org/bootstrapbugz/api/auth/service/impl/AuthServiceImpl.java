package org.bootstrapbugz.api.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.ConfirmRegistrationTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.service.ResetPasswordTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.payload.dto.RefreshTokenDTO;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.AuthService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final MessageService messageService;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final UserMapper userMapper;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final ConfirmRegistrationTokenService confirmRegistrationTokenService;
  private final ResetPasswordTokenService resetPasswordTokenService;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      MessageService messageService,
      PasswordEncoder bCryptPasswordEncoder,
      UserMapper userMapper,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      ConfirmRegistrationTokenService confirmRegistrationTokenService,
      ResetPasswordTokenService resetPasswordTokenService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.messageService = messageService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userMapper = userMapper;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.confirmRegistrationTokenService = confirmRegistrationTokenService;
    this.resetPasswordTokenService = resetPasswordTokenService;
  }

  @Override
  public UserDTO signUp(SignUpRequest signUpRequest) {
    final var user = createUser(signUpRequest);
    final var token = confirmRegistrationTokenService.create(user.getId());
    confirmRegistrationTokenService.sendToEmail(user, token);
    return userMapper.userToUserDTO(user);
  }

  private User createUser(SignUpRequest signUpRequest) {
    final var user =
        new User()
            .setFirstName(signUpRequest.firstName())
            .setLastName(signUpRequest.lastName())
            .setUsername(signUpRequest.username())
            .setEmail(signUpRequest.email())
            .setPassword(bCryptPasswordEncoder.encode(signUpRequest.password()))
            .setRoles(Collections.singleton(getUserRole()));
    return userRepository.save(user);
  }

  private Role getUserRole() {
    return roleRepository
        .findByName(RoleName.USER)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("role.notFound")));
  }

  @Override
  public void resendConfirmationEmail(ResendConfirmationEmailRequest request) {
    final var user =
        userRepository
            .findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    if (user.isActivated())
      throw new ForbiddenException(messageService.getMessage("user.activated"));
    final var token = confirmRegistrationTokenService.create(user.getId());
    confirmRegistrationTokenService.sendToEmail(user, token);
  }

  @Override
  public void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest) {
    final var user =
        userRepository
            .findById(JwtUtil.getUserId(confirmRegistrationRequest.token()))
            .orElseThrow(() -> new ForbiddenException(messageService.getMessage("token.invalid")));
    if (user.isActivated())
      throw new ForbiddenException(messageService.getMessage("user.activated"));
    confirmRegistrationTokenService.check(confirmRegistrationRequest.token());
    user.setActivated(true);
    userRepository.save(user);
  }

  @Override
  public RefreshTokenDTO refreshToken(
      RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
    final var oldRefreshToken = JwtUtil.removeBearer(refreshTokenRequest.refreshToken());
    refreshTokenService.check(oldRefreshToken);
    final var userId = JwtUtil.getUserId(oldRefreshToken);
    final var roleDTOs = JwtUtil.getRoleDTOs(oldRefreshToken);
    refreshTokenService.delete(oldRefreshToken);
    final var accessToken = accessTokenService.create(userId, roleDTOs);
    final var newRefreshToken =
        refreshTokenService.create(userId, roleDTOs, AuthUtil.getUserIpAddress(request));
    return RefreshTokenDTO.builder()
        .accessToken(JwtUtil.addBearer(accessToken))
        .refreshToken(JwtUtil.addBearer(newRefreshToken))
        .build();
  }

  @Override
  public void signOut(HttpServletRequest request) {
    refreshTokenService.deleteByUserAndIpAddress(
        AuthUtil.findSignedInUser().id(), AuthUtil.getUserIpAddress(request));
    accessTokenService.invalidate(
        JwtUtil.removeBearer(AuthUtil.getAccessTokenFromRequest(request)));
  }

  @Override
  public void signOutFromAllDevices() {
    final var userId = AuthUtil.findSignedInUser().id();
    refreshTokenService.deleteAllByUser(userId);
    accessTokenService.invalidateAllByUser(userId);
  }

  @Override
  public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    final var user =
        userRepository
            .findByEmail(forgotPasswordRequest.email())
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    final var token = resetPasswordTokenService.create(user.getId());
    resetPasswordTokenService.sendToEmail(user, token);
  }

  @Override
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    final var user =
        userRepository
            .findById(JwtUtil.getUserId(resetPasswordRequest.token()))
            .orElseThrow(() -> new ForbiddenException(messageService.getMessage("token.invalid")));
    resetPasswordTokenService.check(resetPasswordRequest.token());
    user.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.password()));
    accessTokenService.invalidateAllByUser(user.getId());
    refreshTokenService.deleteAllByUser(user.getId());
    userRepository.save(user);
  }

  @Override
  public UserDTO signedInUser() {
    return AuthUtil.findSignedInUser();
  }

  @Override
  public boolean isUsernameAvailable(String username) {
    return !userRepository.existsByUsername(username);
  }

  @Override
  public boolean isEmailAvailable(String email) {
    return !userRepository.existsByEmail(email);
  }
}
