package org.bootstrapbugz.api.auth.security;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  public UserDetailsServiceImpl(UserRepository userRepository, MessageSource messageSource) {
    this.userRepository = userRepository;
    this.messageSource = messageSource;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) {
    User user =
        userRepository
            .findByUsernameOrEmail(username, username)
            .orElseThrow(
                () ->
                    new ResourceNotFound(
                        messageSource.getMessage(
                            "user.notFound", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.AUTH));
    return UserPrincipal.create(user);
  }
}
