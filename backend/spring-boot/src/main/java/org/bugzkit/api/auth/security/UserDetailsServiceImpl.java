package org.bugzkit.api.auth.security;

import org.bugzkit.api.shared.error.exception.UnauthorizedException;
import org.bugzkit.api.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public UserDetails loadUserByUserId(Long userId) {
    return userRepository
        .findById(userId)
        .map(UserPrincipal::create)
        .orElseThrow(() -> new UnauthorizedException("auth.unauthorized"));
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    return userRepository
        .findByUsernameOrEmail(username, username)
        .map(UserPrincipal::create)
        .orElseThrow(() -> new UnauthorizedException("auth.unauthorized"));
  }
}
