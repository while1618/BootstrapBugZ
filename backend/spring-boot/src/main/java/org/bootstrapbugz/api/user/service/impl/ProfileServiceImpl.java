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
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest;
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
    return UserMapper.INSTANCE.userPrincipalToProfileUserDTO(AuthUtil.findSignedInUser());
  }

  @Override
  public UserDTO patch(PatchProfileRequest patchProfileRequest) {
    final var userId = AuthUtil.findSignedInUser().getId();
    final var user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    if (patchProfileRequest.firstName() != null) user.setFirstName(patchProfileRequest.firstName());
    if (patchProfileRequest.lastName() != null) user.setLastName(patchProfileRequest.lastName());
    tryToSetUsername(user, patchProfileRequest.username());
    tryToSetEmail(user, patchProfileRequest.email());
    return UserMapper.INSTANCE.userToProfileUserDTO(userRepository.save(user));
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
    user.setActive(false);
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    final var token = verificationTokenService.create(user.getId());
    verificationTokenService.sendToEmail(user, token);
  }

  @Override
  public void delete() {
    final var userId = AuthUtil.findSignedInUser().getId();
    accessTokenService.invalidateAllByUserId(userId);
    refreshTokenService.deleteAllByUserId(userId);
    userRepository.deleteById(userId);
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    final var userId = AuthUtil.findSignedInUser().getId();
    final var user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword()))
      throw new BadRequestException(
          "currentPassword", messageService.getMessage("currentPassword.invalid"));
    user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
    accessTokenService.invalidateAllByUserId(userId);
    refreshTokenService.deleteAllByUserId(userId);
    userRepository.save(user);
  }
}
