package org.bootstrapbugz.backend.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.backend.shared.message.service.MessageService;
import org.bootstrapbugz.backend.user.repository.UserRepository;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private static final Marker MARKER = MarkerFactory.getMarker("AUTH");
  private final UserRepository userRepository;
  private final MessageService messageService;

  public UserDetailsServiceImpl(UserRepository userRepository, MessageService messageService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  @Transactional
  public UserDetails loadUserByUserId(Long userId) {
    return userRepository
        .findById(userId)
        .map(UserPrincipal::create)
        .orElseThrow(
            () -> {
              log.error(MARKER, "authentication failed for user: {}", userId);
              return new UnauthorizedException(messageService.getMessage("token.invalid"));
            });
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    return userRepository
        .findByUsernameOrEmail(username, username)
        .map(UserPrincipal::create)
        .orElseThrow(
            () -> {
              log.error(MARKER, "authentication failed for user: {}", username);
              return new UnauthorizedException(messageService.getMessage("token.invalid"));
            });
  }
}
