package org.bootstrapbugz.api.auth.security.user.details;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements CustomUserDetailsService {
  private final UserRepository userRepository;
  private final MessageService messageService;

  public UserDetailsServiceImpl(UserRepository userRepository, MessageService messageService) {
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
                () ->
                    new ResourceNotFoundException(
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    return UserPrincipal.create(user);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    final var user =
        userRepository
            .findByUsernameOrEmail(username, username)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        messageService.getMessage("user.notFound"), ErrorDomain.AUTH));
    return UserPrincipal.create(user);
  }
}
