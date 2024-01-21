package org.bootstrapbugz.api.admin.service.impl;

import java.util.List;
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
    if (userRepository.existsByUsername(userRequest.getUsername()))
      throw new ConflictException(messageService.getMessage("username.exists"));
    if (userRepository.existsByEmail(userRequest.getEmail()))
      throw new ConflictException(messageService.getMessage("email.exists"));
    final var user =
        User.builder()
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .username(userRequest.getUsername())
            .email(userRequest.getEmail())
            .password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
            .active(userRequest.getActive())
            .lock(userRequest.getLock())
            .roles(Set.copyOf(roleRepository.findAllByNameIn(userRequest.getRoleNames())))
            .build();
    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public List<UserDTO> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).stream()
        .map(UserMapper.INSTANCE::userToAdminUserDTO)
        .toList();
  }

  @Override
  public UserDTO findById(Long id) {
    return userRepository
        .findWithRolesById(id)
        .map(UserMapper.INSTANCE::userToAdminUserDTO)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public UserDTO update(Long id, UserRequest userRequest) {
    final var user = userRepository.findWithRolesById(id).orElse(new User());

    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    setUsername(user, userRequest.getUsername());
    setEmail(user, userRequest.getEmail());
    setPassword(user, userRequest.getPassword());
    setActive(user, userRequest.getActive());
    setLock(user, userRequest.getLock());
    setRoles(user, userRequest.getRoleNames());

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO patch(Long id, PatchUserRequest patchUserRequest) {
    final var user =
        userRepository
            .findWithRolesById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));

    if (patchUserRequest.getFirstName() != null) user.setFirstName(patchUserRequest.getFirstName());
    if (patchUserRequest.getLastName() != null) user.setLastName(patchUserRequest.getLastName());
    if (patchUserRequest.getUsername() != null) setUsername(user, patchUserRequest.getUsername());
    if (patchUserRequest.getEmail() != null) setEmail(user, patchUserRequest.getEmail());
    if (patchUserRequest.getPassword() != null) setPassword(user, patchUserRequest.getPassword());
    if (patchUserRequest.getActive() != null) setActive(user, patchUserRequest.getActive());
    if (patchUserRequest.getLock() != null) setLock(user, patchUserRequest.getLock());
    if (patchUserRequest.getRoleNames() != null) setRoles(user, patchUserRequest.getRoleNames());

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  private void deleteAuthTokens(Long userId) {
    accessTokenService.invalidateAllByUserId(userId);
    refreshTokenService.deleteAllByUserId(userId);
  }

  private void setUsername(User user, String username) {
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new ConflictException("username", messageService.getMessage("username.exists"));

    user.setUsername(username);
  }

  private void setEmail(User user, String email) {
    if (user.getEmail().equals(email)) return;
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
