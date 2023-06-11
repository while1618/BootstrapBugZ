package org.bootstrapbugz.api.admin.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.admin.service.UserService;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("adminUserService")
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final MessageService messageService;

  public UserServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      PasswordEncoder bCryptPasswordEncoder,
      MessageService messageService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.messageService = messageService;
  }

  @Override
  public UserDTO create(UserRequest userRequest) {
    if (userRepository.existsByUsername(userRequest.username()))
      throw new ConflictException(messageService.getMessage("username.exists"));
    if (userRepository.existsByEmail(userRequest.email()))
      throw new ConflictException(messageService.getMessage("email.exists"));
    final var user =
        User.builder()
            .firstName(userRequest.firstName())
            .lastName(userRequest.lastName())
            .username(userRequest.username())
            .email(userRequest.email())
            .password(bCryptPasswordEncoder.encode(userRequest.password()))
            .active(userRequest.active())
            .lock(userRequest.lock())
            .roles(Set.copyOf(roleRepository.findAllByNameIn(userRequest.roleNames())))
            .build();
    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAllWithRoles().stream()
        .map(UserMapper.INSTANCE::userToAdminUserDTO)
        .toList();
  }

  @Override
  public UserDTO findById(Long id) {
    return userRepository
        .findByIdWithRoles(id)
        .map(UserMapper.INSTANCE::userToAdminUserDTO)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public UserDTO update(Long id, UserRequest userRequest) {
    final var user = userRepository.findByIdWithRoles(id).orElse(User.builder().id(id).build());

    if (userRepository.existsByUsername(userRequest.username())
        && Objects.equals(user.getUsername(), userRequest.username()))
      throw new ConflictException(messageService.getMessage("username.exists"));
    if (userRepository.existsByEmail(userRequest.email())
        && Objects.equals(user.getEmail(), userRequest.email()))
      throw new ConflictException(messageService.getMessage("email.exists"));

    final var roles = Set.copyOf(roleRepository.findAllByNameIn(userRequest.roleNames()));
    user.setFirstName(userRequest.firstName());
    user.setLastName(userRequest.lastName());
    user.setUsername(userRequest.username());
    user.setEmail(userRequest.email());
    user.setPassword(bCryptPasswordEncoder.encode(userRequest.password()));
    user.setActive(userRequest.active());
    user.setLock(userRequest.lock());
    user.setRoles(roles);

    if (!bCryptPasswordEncoder.matches(userRequest.password(), user.getPassword())
        || !userRequest.active()
        || userRequest.lock()
        || !roles.equals(user.getRoles())) {
      accessTokenService.invalidateAllByUserId(id);
      refreshTokenService.deleteAllByUserId(id);
    }

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO patch(Long id, PatchUserRequest patchUserRequest) {
    final var user =
        userRepository
            .findByIdWithRoles(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));

    final var roles = Set.copyOf(roleRepository.findAllByNameIn(patchUserRequest.roleNames()));
    if (patchUserRequest.firstName() != null) user.setFirstName(patchUserRequest.firstName());
    if (patchUserRequest.lastName() != null) user.setLastName(patchUserRequest.lastName());
    tryToSetUsername(user, patchUserRequest.username());
    tryToSetEmail(user, patchUserRequest.email());
    if (patchUserRequest.password() != null)
      user.setPassword(bCryptPasswordEncoder.encode(patchUserRequest.password()));
    if (patchUserRequest.active() != null) user.setActive(patchUserRequest.active());
    if (patchUserRequest.lock() != null) user.setLock(patchUserRequest.lock());
    if (patchUserRequest.roleNames() != null) user.setRoles(roles);

    if (!bCryptPasswordEncoder.matches(patchUserRequest.password(), user.getPassword())
        || Boolean.FALSE.equals(patchUserRequest.active())
        || Boolean.TRUE.equals(patchUserRequest.lock())
        || (patchUserRequest.roleNames() != null && !roles.equals(user.getRoles()))) {
      accessTokenService.invalidateAllByUserId(id);
      refreshTokenService.deleteAllByUserId(id);
    }

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
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
  }

  @Override
  public void delete(Long id) {
    accessTokenService.invalidateAllByUserId(id);
    refreshTokenService.deleteAllByUserId(id);
    userRepository.deleteById(id);
  }
}
