package org.bootstrapbugz.api.admin.service.impl;

import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public AdminServiceImpl(UserRepository userRepository, JwtService jwtService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  public void activate(AdminRequest adminRequest) {
    final var users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(user -> user.setActivated(true));
    userRepository.saveAll(users);
  }

  @Override
  public void deactivate(AdminRequest adminRequest) {
    final var users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          user.setActivated(false);
          jwtService.invalidateAllTokens(user.getId());
          jwtService.deleteAllRefreshTokensByUser(user.getId());
        });
    userRepository.saveAll(users);
  }

  @Override
  public void unlock(AdminRequest adminRequest) {
    final var users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(user -> user.setNonLocked(true));
    userRepository.saveAll(users);
  }

  @Override
  public void lock(AdminRequest adminRequest) {
    final var users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          user.setNonLocked(false);
          jwtService.invalidateAllTokens(user.getId());
          jwtService.deleteAllRefreshTokensByUser(user.getId());
        });
    userRepository.saveAll(users);
  }

  @Override
  public void updateRole(UpdateRoleRequest updateRoleRequest) {
    final var users = userRepository.findAllByUsernameIn(updateRoleRequest.getUsernames());
    users.forEach(
        user -> {
          final var roles =
              updateRoleRequest.getRoleNames().stream().map(Role::new).collect(Collectors.toSet());
          user.setRoles(roles);
          jwtService.invalidateAllTokens(user.getId());
          jwtService.deleteAllRefreshTokensByUser(user.getId());
        });
    userRepository.saveAll(users);
  }

  @Override
  @Transactional
  public void delete(AdminRequest adminRequest) {
    final var users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          jwtService.invalidateAllTokens(user.getId());
          jwtService.deleteAllRefreshTokensByUser(user.getId());
          userRepository.delete(user);
        });
  }
}
