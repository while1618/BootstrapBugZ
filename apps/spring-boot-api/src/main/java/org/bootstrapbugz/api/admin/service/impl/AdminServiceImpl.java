package org.bootstrapbugz.api.admin.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.dto.UserDto;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JwtService jwtService;
  private final MessageService messageService;
  private final UserMapper userMapper;

  public AdminServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      JwtService jwtService,
      MessageService messageService,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.jwtService = jwtService;
    this.messageService = messageService;
    this.userMapper = userMapper;
  }

  @Override
  public List<UserDto> findAllUsers() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty())
      throw new ResourceNotFound(messageService.getMessage("users.notFound"), ErrorDomain.USER);
    return userMapper.usersToUserDtos(users);
  }

  @Override
  public void changeRole(ChangeRoleRequest changeRoleRequest) {
    List<User> users = userRepository.findAllByUsernameIn(changeRoleRequest.getUsernames());
    users.forEach(
        user -> {
          Set<Role> roles =
              changeRoleRequest.getRoleNames().stream().map(Role::new).collect(Collectors.toSet());
          user.setRoles(roles);
          jwtService.invalidateAllTokens(user.getUsername());
        });
    userRepository.saveAll(users);
  }

  @Override
  public void lock(AdminRequest adminRequest) {
    List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          user.setNonLocked(false);
          jwtService.invalidateAllTokens(user.getUsername());
        });
    userRepository.saveAll(users);
  }

  @Override
  public void unlock(AdminRequest adminRequest) {
    List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(user -> user.setNonLocked(true));
    userRepository.saveAll(users);
  }

  @Override
  public void activate(AdminRequest adminRequest) {
    List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(user -> user.setActivated(true));
    userRepository.saveAll(users);
  }

  @Override
  public void deactivate(AdminRequest adminRequest) {
    List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          user.setActivated(false);
          jwtService.invalidateAllTokens(user.getUsername());
        });
    userRepository.saveAll(users);
  }

  @Override
  @Transactional
  public void delete(AdminRequest adminRequest) {
    List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
    users.forEach(
        user -> {
          jwtService.invalidateAllTokens(user.getUsername());
          userRepository.delete(user);
        });
  }
}
