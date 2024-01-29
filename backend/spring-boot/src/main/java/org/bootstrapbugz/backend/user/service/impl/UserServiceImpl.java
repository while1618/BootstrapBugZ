package org.bootstrapbugz.backend.user.service.impl;

import java.util.List;
import org.bootstrapbugz.backend.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.backend.shared.message.service.MessageService;
import org.bootstrapbugz.backend.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.backend.user.mapper.UserMapper;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.backend.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.backend.user.repository.UserRepository;
import org.bootstrapbugz.backend.user.service.UserService;
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
