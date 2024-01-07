package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.data.domain.Pageable;
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
  public List<UserDTO> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).stream()
        .map(UserMapper.INSTANCE::userToSimpleUserDTO)
        .toList();
  }

  @Override
  public UserDTO findById(Long id) {
    return userRepository
        .findById(id)
        .map(UserMapper.INSTANCE::userToSimpleUserDTO)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public UserDTO findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .map(UserMapper.INSTANCE::userToSimpleUserDTO)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("user.notFound")));
  }

  @Override
  public AvailabilityDTO usernameAvailability(
      UsernameAvailabilityRequest usernameAvailabilityRequest) {
    final var available = !userRepository.existsByUsername(usernameAvailabilityRequest.username());
    return new AvailabilityDTO(available);
  }

  @Override
  public AvailabilityDTO emailAvailability(EmailAvailabilityRequest emailAvailabilityRequest) {
    final var available = !userRepository.existsByEmail(emailAvailabilityRequest.email());
    return new AvailabilityDTO(available);
  }
}
