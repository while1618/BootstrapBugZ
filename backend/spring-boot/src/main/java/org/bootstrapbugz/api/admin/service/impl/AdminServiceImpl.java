package org.bootstrapbugz.api.admin.service.impl;

import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final MessageService messageService;

  public AdminServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      MessageService messageService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.messageService = messageService;
  }

  private User getUser(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public void activate(String username) {
    final var user = getUser(username);
    user.setActivated(true);
    userRepository.save(user);
  }

  @Override
  public void deactivate(String username) {
    final var user = getUser(username);
    user.setActivated(false);
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.save(user);
  }

  @Override
  public void unlock(String username) {
    final var user = getUser(username);
    user.setNonLocked(true);
    userRepository.save(user);
  }

  @Override
  public void lock(String username) {
    final var user = getUser(username);
    user.setNonLocked(false);
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.save(user);
  }

  @Override
  public void updateRole(String username, UpdateRoleRequest updateRoleRequest) {
    final var user = getUser(username);
    final var roles = roleRepository.findAllByNameIn(updateRoleRequest.roleNames());
    user.setRoles(Set.copyOf(roles));
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void delete(String username) {
    final var user = userRepository.findByUsername(username).orElse(null);
    if (user == null) return;
    accessTokenService.invalidateAllByUserId(user.getId());
    refreshTokenService.deleteAllByUserId(user.getId());
    userRepository.delete(user);
  }
}
