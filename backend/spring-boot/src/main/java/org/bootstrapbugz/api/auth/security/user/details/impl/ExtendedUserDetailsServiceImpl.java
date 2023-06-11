package org.bootstrapbugz.api.auth.security.user.details.impl;

import org.bootstrapbugz.api.auth.security.user.details.ExtendedUserDetailsService;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExtendedUserDetailsServiceImpl implements ExtendedUserDetailsService {
  private final UserRepository userRepository;
  private final MessageService messageService;

  public ExtendedUserDetailsServiceImpl(
      UserRepository userRepository, MessageService messageService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUserId(Long userId) {
    final var user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    return UserPrincipal.create(user);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    final var user =
        userRepository
            .findByUsernameOrEmail(username, username)
            .orElseThrow(
                () -> new UnauthorizedException(messageService.getMessage("token.invalid")));
    return UserPrincipal.create(user);
  }
}
