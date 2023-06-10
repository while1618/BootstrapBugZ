package org.bootstrapbugz.api.admin.service.impl;

import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.SaveUserRequest;
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
  public List<UserDTO> findAll() {
    return userRepository.findAllWithRoles().stream()
        .map(UserMapper.INSTANCE::userToUserDTO)
        .toList();
  }

  @Override
  public UserDTO findById(Long id) {
    return userRepository
        .findByIdWithRoles(id)
        .map(UserMapper.INSTANCE::userToUserDTO)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public UserDTO create(SaveUserRequest saveRequest) {
    if (userRepository.existsByUsername(saveRequest.username()))
      throw new ConflictException(messageService.getMessage("username.exists"));
    if (userRepository.existsByEmail(saveRequest.email()))
      throw new ConflictException(messageService.getMessage("email.exists"));
    final var user = createUser(saveRequest);
    return UserMapper.INSTANCE.userToUserDTO(userRepository.save(user));
  }

  @Override
  public UserDTO update(Long id, SaveUserRequest saveRequest) {
    return null;
  }

  private User createUser(SaveUserRequest saveUserRequest) {
    final var roles = roleRepository.findAllByNameIn(saveUserRequest.roleNames());
    return User.builder()
        .firstName(saveUserRequest.firstName())
        .lastName(saveUserRequest.lastName())
        .username(saveUserRequest.username())
        .email(saveUserRequest.email())
        .password(bCryptPasswordEncoder.encode(saveUserRequest.password()))
        .activated(saveUserRequest.active())
        .nonLocked(saveUserRequest.lock())
        .roles(Set.copyOf(roles))
        .build();
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
