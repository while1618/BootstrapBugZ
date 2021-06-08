package org.bootstrapbugz.api.auth.util;

import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  private AuthUtil() {}

  public static User findLoggedUser(UserRepository userRepository, MessageService messageService) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository
        .findByUsername(auth.getName())
        .orElseThrow(
            () ->
                new ResourceNotFound(messageService.getMessage("user.notFound"), ErrorDomain.USER));
  }
}
