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
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
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
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final ConfirmRegistrationTokenService confirmRegistrationTokenService;
  private final ResetPasswordTokenService resetPasswordTokenService;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      MessageService messageService,
      PasswordEncoder bCryptPasswordEncoder,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      ConfirmRegistrationTokenService confirmRegistrationTokenService,
      ResetPasswordTokenService resetPasswordTokenService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.messageService = messageService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
    return UserMapper.INSTANCE.userToUserDTO(user);
  }

  private User createUser(SignUpRequest signUpRequest) {
    final var user =
        User.builder()
            .firstName(signUpRequest.firstName())
            .lastName(signUpRequest.lastName())
            .username(signUpRequest.username())
            .email(signUpRequest.email())
            .password(bCryptPasswordEncoder.encode(signUpRequest.password()))
            .roles(Collections.singleton(getUserRole()))
            .build();
    return userRepository.save(user);
  }

  private Role getUserRole() {
    return roleRepository
        .findByName(RoleName.USER)
        .orElseThrow(() -> new RuntimeException(messageService.getMessage("role.notFound")));
  }

  @Override
  public void resendConfirmationEmail(ResendConfirmationEmailRequest request) {
    final var user =
        userRepository
            .findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    if (user.isActivated())
      throw new ConflictException(messageService.getMessage("user.activated"));
    final var token = confirmRegistrationTokenService.create(user.getId());
    confirmRegistrationTokenService.sendToEmail(user, token);
  }

  @Override
  public void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest) {
    final var user =
        userRepository
            .findById(JwtUtil.getUserId(confirmRegistrationRequest.token()))
            .orElseThrow(
                () -> new BadRequestException("token", messageService.getMessage("token.invalid")));
    if (user.isActivated())
      throw new ConflictException(messageService.getMessage("user.activated"));
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
    final var id = AuthUtil.findSignedInUser().id();
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var accessToken = JwtUtil.removeBearer(AuthUtil.getAccessTokenFromRequest(request));
    refreshTokenService.deleteByUserIdAndIpAddress(id, ipAddress);
    accessTokenService.invalidate(accessToken);
  }

  @Override
  public void signOutFromAllDevices() {
    final var userId = AuthUtil.findSignedInUser().id();
    refreshTokenService.deleteAllByUserId(userId);
    accessTokenService.invalidateAllByUserId(userId);
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
            .orElseThrow(
                () -> new BadRequestException("token", messageService.getMessage("token.invalid")));
    resetPasswordTokenService.check(resetPasswordRequest.token());
    user.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.password()));
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
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
