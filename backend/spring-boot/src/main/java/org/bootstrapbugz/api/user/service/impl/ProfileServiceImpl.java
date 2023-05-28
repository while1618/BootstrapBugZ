package org.bootstrapbugz.api.user.service.impl;

import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.ConfirmRegistrationTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
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
  private final ConfirmRegistrationTokenService confirmRegistrationTokenService;

  public ProfileServiceImpl(
      UserRepository userRepository,
      MessageService messageService,
      PasswordEncoder bCryptPasswordEncoder,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      ConfirmRegistrationTokenService confirmRegistrationTokenService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.confirmRegistrationTokenService = confirmRegistrationTokenService;
  }

  @Override
  public UserDTO update(UpdateProfileRequest updateProfileRequest) {
    final var userDTO = AuthUtil.findSignedInUser();
    final var user =
        userRepository
            .findByUsernameWithRoles(userDTO.username())
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    user.setFirstName(updateProfileRequest.firstName());
    user.setLastName(updateProfileRequest.lastName());
    tryToSetUsername(user, updateProfileRequest.username());
    tryToSetEmail(user, updateProfileRequest.email());
    return UserMapper.INSTANCE.userToUserDTO(userRepository.save(user));
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
    accessTokenService.invalidateAllByUser(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    final var token = confirmRegistrationTokenService.create(user.getId());
    confirmRegistrationTokenService.sendToEmail(user, token);
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    final var userDTO = AuthUtil.findSignedInUser();
    final var user =
        userRepository
            .findByUsername(userDTO.username())
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword()))
      throw new BadRequestException(
          "oldPassword", messageService.getMessage("oldPassword.invalid"));
    user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
    accessTokenService.invalidateAllByUser(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.save(user);
  }

  @Override
  public void delete() {
    final var userDTO = AuthUtil.findSignedInUser();
    accessTokenService.invalidateAllByUser(userDTO.id());
    refreshTokenService.deleteAllByUserId(userDTO.id());
    userRepository.deleteById(userDTO.id());
  }
}
