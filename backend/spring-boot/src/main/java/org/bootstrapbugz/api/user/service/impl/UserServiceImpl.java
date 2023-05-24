package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final MessageService messageService;

  public UserServiceImpl(UserRepository userRepository, MessageService messageService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  @Override
  public List<UserDTO> findAll() {
    if (AuthUtil.isAdminSignedIn()) return usersForAdmin();
    return usersForNonAdmin();
  }

  private List<UserDTO> usersForAdmin() {
    return userRepository.findAllWithRoles().stream()
        .map(UserMapper.INSTANCE::userToUserDTO)
        .toList();
  }

  private List<UserDTO> usersForNonAdmin() {
    return userRepository.findAll().stream()
        .map(
            user -> {
              user.setRoles(null);
              user.setEmail(null);
              return UserMapper.INSTANCE.userToUserDTO(user);
            })
        .toList();
  }

  @Override
  public UserDTO findByUsername(String username) {
    if (AuthUtil.isAdminSignedIn())
      return UserMapper.INSTANCE.userToUserDTO(userForAdmin(username));
    return UserMapper.INSTANCE.userToUserDTO(userForNonAdmin(username));
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
    if (!isUserSameAsSignedIn(user.getUsername())) user.setEmail(null);
    return user;
  }

  private boolean isUserSameAsSignedIn(String username) {
    return AuthUtil.isSignedIn() && AuthUtil.findSignedInUser().username().equals(username);
  }
}
