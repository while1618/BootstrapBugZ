package org.bootstrapbugz.api.auth.util;

import java.util.stream.Collectors;
import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.dto.UserDto.RoleDto;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  private AuthUtil() {}

  public static User findLoggedUser(UserRepository userRepository, MessageService messageService) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userPrincipalToUser((UserPrincipal) auth.getPrincipal());
  }

  public static User userPrincipalToUser(UserPrincipal userPrincipal) {
    return new User()
        .setId(userPrincipal.getId())
        .setFirstName(userPrincipal.getFirstName())
        .setLastName(userPrincipal.getLastName())
        .setUsername(userPrincipal.getUsername())
        .setEmail(userPrincipal.getEmail())
        .setActivated(userPrincipal.isEnabled())
        .setNonLocked(userPrincipal.isAccountNonLocked());
  }
}
