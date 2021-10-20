package org.bootstrapbugz.api.user.service.impl;

import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final UserMapper userMapper;

  public UserServiceImpl(
      UserRepository userRepository, MessageService messageService, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.userMapper = userMapper;
  }

  @Override
  public List<UserResponse> findAll() {
    if (AuthUtil.isSignedIn()
        && AuthUtil.findSignedInUser().getRoles().contains(new Role(Role.RoleName.ADMIN)))
      return userRepository.findAllWithRoles().stream()
          .map(userMapper::userToUserResponse)
          .toList();
    return userRepository.findAll().stream()
        .map(
            user -> {
              user.setRoles(null);
              user.setEmail(null);
              return userMapper.userToUserResponse(user);
            })
        .toList();
  }

  @Override
  public UserResponse findByUsername(String username) {
    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        messageService.getMessage("user.notFound"), ErrorDomain.USER));
    user.setRoles(null);
    if (!AuthUtil.isSignedIn()
        || !AuthUtil.findSignedInUser().getUsername().equals(user.getUsername()))
      user.setEmail(null);
    return userMapper.userToUserResponse(user);
  }
}
