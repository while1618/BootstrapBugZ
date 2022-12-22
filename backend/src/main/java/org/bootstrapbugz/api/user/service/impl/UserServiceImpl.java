package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.stereotype.Service;

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
  public List<UserDto> findAll() {
    if (AuthUtil.isAdminSignedIn())
      return userRepository.findAllWithRoles().stream().map(userMapper::userToUserDto).toList();
    return userRepository.findAll().stream()
        .map(
            user -> {
              user.setRoles(null);
              user.setEmail(null);
              return userMapper.userToUserDto(user);
            })
        .toList();
  }

  @Override
  public UserDto findByUsername(String username) {
    if (AuthUtil.isAdminSignedIn()) return userMapper.userToUserDto(userForAdmin(username));
    return userMapper.userToUserDto(userForNonAdmin(username));
  }

  private User userForAdmin(String username) {
    return userRepository
        .findByUsernameWithRoles(username)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  private User userForNonAdmin(String username) {
    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    user.setRoles(null);
    if (!(AuthUtil.isSignedIn()
        && AuthUtil.findSignedInUser().getUsername().equals(user.getUsername())))
      user.setEmail(null);
    return user;
  }
}
