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
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("adminUserService")
@PreAuthorize("hasAuthority('ADMIN')")
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
  public List<UserDTO> findAll(Pageable pageable) {
    return userRepository.findAllWithRoles(pageable).stream()
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
    final var user = userRepository.findByIdWithRoles(id).orElse(new User());

    user.setFirstName(userRequest.firstName());
    user.setLastName(userRequest.lastName());
    setUsername(user, userRequest.username());
    setEmail(user, userRequest.email());
    setPassword(user, userRequest.password());
    setActive(user, userRequest.active());
    setLock(user, userRequest.lock());
    setRoles(user, userRequest.roleNames());

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO patch(Long id, PatchUserRequest patchUserRequest) {
    final var user =
        userRepository
            .findByIdWithRoles(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));

    if (patchUserRequest.firstName() != null) user.setFirstName(patchUserRequest.firstName());
    if (patchUserRequest.lastName() != null) user.setLastName(patchUserRequest.lastName());
    if (patchUserRequest.username() != null) setUsername(user, patchUserRequest.username());
    if (patchUserRequest.email() != null) setEmail(user, patchUserRequest.email());
    if (patchUserRequest.password() != null) setPassword(user, patchUserRequest.password());
    if (patchUserRequest.active() != null) setActive(user, patchUserRequest.active());
    if (patchUserRequest.lock() != null) setLock(user, patchUserRequest.lock());
    if (patchUserRequest.roleNames() != null) setRoles(user, patchUserRequest.roleNames());

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  private void deleteAuthTokens(Long userId) {
    accessTokenService.invalidateAllByUserId(userId);
    refreshTokenService.deleteAllByUserId(userId);
  }

  private void setUsername(User user, String username) {
    if (Objects.equals(user.getUsername(), username)) return;
    if (userRepository.existsByUsername(username))
      throw new ConflictException("username", messageService.getMessage("username.exists"));

    user.setUsername(username);
  }

  private void setEmail(User user, String email) {
    if (Objects.equals(user.getEmail(), email)) return;
    if (userRepository.existsByEmail(email))
      throw new ConflictException("email", messageService.getMessage("email.exists"));

    user.setEmail(email);
  }

  private void setPassword(User user, String password) {
    if (bCryptPasswordEncoder.matches(password, user.getPassword())) return;

    user.setPassword(bCryptPasswordEncoder.encode(password));
    deleteAuthTokens(user.getId());
  }

  private void setActive(User user, Boolean active) {
    if (Objects.equals(user.getActive(), active)) return;

    user.setActive(active);
    if (Boolean.FALSE.equals(active)) deleteAuthTokens(user.getId());
  }

  private void setLock(User user, Boolean lock) {
    if (Objects.equals(user.getLock(), lock)) return;

    user.setLock(lock);
    if (Boolean.TRUE.equals(lock)) deleteAuthTokens(user.getId());
  }

  private void setRoles(User user, Set<RoleName> roleNames) {
    final var roles = Set.copyOf(roleRepository.findAllByNameIn(roleNames));
    if (Objects.equals(user.getRoles(), roles)) return;

    user.setRoles(roles);
    deleteAuthTokens(user.getId());
  }

  @Override
  public void delete(Long id) {
    deleteAuthTokens(id);
    userRepository.deleteById(id);
  }
}
