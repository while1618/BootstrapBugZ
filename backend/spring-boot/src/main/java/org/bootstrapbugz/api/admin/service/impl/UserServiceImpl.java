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
        && !user.getUsername().equals(userRequest.username()))
      throw new ConflictException(messageService.getMessage("username.exists"));
    if (userRepository.existsByEmail(userRequest.email())
        && !user.getEmail().equals(userRequest.email()))
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

    if (!userRequest.active() || userRequest.lock() || !roles.equals(user.getRoles())) {
      accessTokenService.invalidateAllByUserId(id);
      refreshTokenService.deleteAllByUserId(id);
    }

    return UserMapper.INSTANCE.userToAdminUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO patch(Long id, PatchUserRequest patchUserRequest) {
    return null;
  }

  @Override
  public void delete(Long id) {
    accessTokenService.invalidateAllByUserId(id);
    refreshTokenService.deleteAllByUserId(id);
    userRepository.deleteById(id);
  }
}
