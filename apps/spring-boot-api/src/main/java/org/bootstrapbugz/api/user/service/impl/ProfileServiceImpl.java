package org.bootstrapbugz.api.user.service.impl;

import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.ProfileService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final UserMapper userMapper;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final JwtService jwtService;

  public ProfileServiceImpl(
      UserRepository userRepository,
      MessageService messageService,
      UserMapper userMapper,
      PasswordEncoder bCryptPasswordEncoder,
      ApplicationEventPublisher eventPublisher,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.userMapper = userMapper;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.eventPublisher = eventPublisher;
    this.jwtService = jwtService;
  }

  @Override
  public UserResponse update(UpdateProfileRequest updateProfileRequest) {
    final var user = AuthUtil.findSignedInUser();
    user.setFirstName(updateProfileRequest.getFirstName());
    user.setLastName(updateProfileRequest.getLastName());
    tryToSetUsername(user, updateProfileRequest.getUsername());
    tryToSetEmail(user, updateProfileRequest.getEmail());
    return userMapper.userToUserResponse(userRepository.save(user));
  }

  private void tryToSetUsername(User user, String username) {
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new ConflictException("username", messageService.getMessage("username.exists"));

    user.setUsername(username);
  }

  private void tryToSetEmail(User user, String email) {
    if (user.getEmail().equals(email)) return;
    if (userRepository.existsByEmail(email))
      throw new ConflictException("email", messageService.getMessage("email.exists"));

    user.setEmail(email);
    user.setActivated(false);
    jwtService.invalidateAllTokens(user.getId());
    jwtService.deleteAllRefreshTokensByUser(user.getId());

    final String token =
        jwtService.createToken(user.getId(), JwtUtil.JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(
        new OnSendJwtEmail(user, token, JwtUtil.JwtPurpose.CONFIRM_REGISTRATION));
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    final var user = AuthUtil.findSignedInUser();
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
      throw new BadRequestException(
          "oldPassword", messageService.getMessage("oldPassword.invalid"));
    user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
    jwtService.invalidateAllTokens(user.getId());
    jwtService.deleteAllRefreshTokensByUser(user.getId());
    userRepository.save(user);
  }
}
