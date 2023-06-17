package org.bootstrapbugz.api.auth.security;

import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
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
        .orElseThrow(() -> new UnauthorizedException(messageService.getMessage("token.invalid")));
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    return userRepository
        .findByUsernameOrEmail(username, username)
        .map(UserPrincipal::create)
        .orElseThrow(() -> new UnauthorizedException(messageService.getMessage("token.invalid")));
  }
}
