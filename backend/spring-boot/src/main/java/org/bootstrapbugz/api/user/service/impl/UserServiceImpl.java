package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
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
    return userRepository.findAll().stream()
        .map(
            user -> {
              user.setEmail(null);
              user.setActivated(null);
              user.setNonLocked(null);
              user.setRoles(null);
              return UserMapper.INSTANCE.userToUserDTO(user);
            })
        .toList();
  }

  @Override
  public UserDTO findById(Long id) {
    final var user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    user.setEmail(null);
    user.setActivated(null);
    user.setNonLocked(null);
    user.setRoles(null);
    return UserMapper.INSTANCE.userToUserDTO(user);
  }

  @Override
  public UserDTO findByUsername(String username) {
    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
    user.setEmail(null);
    user.setActivated(null);
    user.setNonLocked(null);
    user.setRoles(null);
    return UserMapper.INSTANCE.userToUserDTO(user);
  }
}
