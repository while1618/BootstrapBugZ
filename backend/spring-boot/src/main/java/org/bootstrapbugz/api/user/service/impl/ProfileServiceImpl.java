package org.bootstrapbugz.api.user.service.impl;

import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.service.VerificationTokenService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.ProfileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final VerificationTokenService verificationTokenService;

  public ProfileServiceImpl(
      UserRepository userRepository,
      MessageService messageService,
      PasswordEncoder bCryptPasswordEncoder,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      VerificationTokenService verificationTokenService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.verificationTokenService = verificationTokenService;
  }

  @Override
  public UserDTO find() {
    return AuthUtil.findSignedInUser();
  }

  @Override
  public UserDTO patch(UpdateProfileRequest updateProfileRequest) {
    final var userDTO = AuthUtil.findSignedInUser();
    final var user =
        userRepository
            .findById(userDTO.id())
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    if (updateProfileRequest.firstName() != null)
      user.setFirstName(updateProfileRequest.firstName());
    if (updateProfileRequest.lastName() != null) user.setLastName(updateProfileRequest.lastName());
    tryToSetUsername(user, updateProfileRequest.username());
    tryToSetEmail(user, updateProfileRequest.email());
    return UserMapper.INSTANCE.userToUserDTO(userRepository.save(user));
  }

  private void tryToSetUsername(User user, String username) {
    if (username == null) return;
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new ConflictException("username", messageService.getMessage("username.exists"));

    user.setUsername(username);
  }

  private void tryToSetEmail(User user, String email) {
    if (email == null) return;
    if (user.getEmail().equals(email)) return;
    if (userRepository.existsByEmail(email))
      throw new ConflictException("email", messageService.getMessage("email.exists"));

    user.setEmail(email);
    user.setActivated(false);
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    final var token = verificationTokenService.create(user.getId());
    verificationTokenService.sendToEmail(user, token);
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    final var userDTO = AuthUtil.findSignedInUser();
    final var user =
        userRepository
            .findById(userDTO.id())
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword()))
      throw new BadRequestException(
          "currentPassword", messageService.getMessage("oldPassword.invalid"));
    user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.save(user);
  }

  @Override
  public void delete() {
    final var userDTO = AuthUtil.findSignedInUser();
    accessTokenService.invalidateAllByUserId(userDTO.id());
    refreshTokenService.deleteAllByUserId(userDTO.id());
    userRepository.deleteById(userDTO.id());
  }
}
