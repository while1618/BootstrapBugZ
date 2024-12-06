package org.bootstrapbugz.backend.admin.service.impl;

import java.util.Set;
import org.bootstrapbugz.backend.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.backend.admin.payload.request.UserRequest;
import org.bootstrapbugz.backend.admin.service.UserService;
import org.bootstrapbugz.backend.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.backend.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.backend.shared.error.exception.BadRequestException;
import org.bootstrapbugz.backend.shared.error.exception.ConflictException;
import org.bootstrapbugz.backend.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.backend.shared.payload.dto.PageableDTO;
import org.bootstrapbugz.backend.user.mapper.UserMapper;
import org.bootstrapbugz.backend.user.model.Role.RoleName;
import org.bootstrapbugz.backend.user.model.User;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.repository.RoleRepository;
import org.bootstrapbugz.backend.user.repository.UserRepository;
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

  public UserServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      PasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  public UserDTO create(UserRequest userRequest) {
    if (userRepository.existsByUsername(userRequest.username()))
      throw new ConflictException("user.usernameExists");
    if (userRepository.existsByEmail(userRequest.email()))
      throw new ConflictException("user.emailExists");
    final var user =
        User.builder()
            .username(userRequest.username())
            .email(userRequest.email())
            .password(bCryptPasswordEncoder.encode(userRequest.password()))
            .active(userRequest.active())
            .lock(userRequest.lock())
            .roles(Set.copyOf(roleRepository.findAllByNameIn(userRequest.roleNames())))
            .build();
    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  /*
   * This method is using two query calls because of HHH000104 warning
   * https://vladmihalcea.com/fix-hibernate-hhh000104-entity-fetch-pagination-warning-message/
   * https://vladmihalcea.com/join-fetch-pagination-spring/
   * */
  @Override
  public PageableDTO<UserDTO> findAll(Pageable pageable) {
    final var userIds = userRepository.findAllUserIds(pageable);
    final var users =
        userRepository.findAllByIdIn(userIds).stream()
            .map(UserMapper.INSTANCE::userToAdminUserDTO)
            .toList();
    final long total = userRepository.count();
    return new PageableDTO<>(users, total);
  }

  @Override
  public UserDTO findById(Long id) {
    return userRepository
        .findWithRolesById(id)
        .map(UserMapper.INSTANCE::userToAdminUserDTO)
        .orElseThrow(() -> new ResourceNotFoundException("user.notFound"));
  }

  @Override
  public UserDTO update(Long id, UserRequest userRequest) {
    final var user = userRepository.findWithRolesById(id).orElse(new User());

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
            .findWithRolesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("user.notFound"));

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
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new ConflictException("user.usernameExists");

    user.setUsername(username);
  }

  private void setEmail(User user, String email) {
    if (user.getEmail().equals(email)) return;
    if (userRepository.existsByEmail(email)) throw new ConflictException("user.emailExists");

    user.setEmail(email);
  }

  private void setPassword(User user, String password) {
    if (bCryptPasswordEncoder.matches(password, user.getPassword())) return;

    user.setPassword(bCryptPasswordEncoder.encode(password));
    deleteAuthTokens(user.getId());
  }

  private void setActive(User user, Boolean active) {
    if (user.getActive().equals(active)) return;

    user.setActive(active);
    if (Boolean.FALSE.equals(active)) deleteAuthTokens(user.getId());
  }

  private void setLock(User user, Boolean lock) {
    if (user.getLock().equals(lock)) return;

    user.setLock(lock);
    if (Boolean.TRUE.equals(lock)) deleteAuthTokens(user.getId());
  }

  private void setRoles(User user, Set<RoleName> roleNames) {
    if (roleNames.isEmpty()) throw new BadRequestException("user.rolesEmpty");

    final var roles = Set.copyOf(roleRepository.findAllByNameIn(roleNames));
    if (user.getRoles().equals(roles)) return;

    user.setRoles(roles);
    deleteAuthTokens(user.getId());
  }

  @Override
  public void delete(Long id) {
    deleteAuthTokens(id);
    userRepository.deleteById(id);
  }
}
