package org.bootstrapbugz.api.auth.util;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  public static final String AUTH_HEADER = "Authorization";

  private AuthUtil() {}

  public static User findLoggedUser() {
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
        .setPassword(userPrincipal.getPassword())
        .setActivated(userPrincipal.isEnabled())
        .setNonLocked(userPrincipal.isAccountNonLocked())
        .setRoles(
            userPrincipal.getAuthorities().stream()
                .map(authority -> new Role(RoleName.valueOf(authority.getAuthority())))
                .collect(Collectors.toSet()));
  }

  public static String getUserIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.isEmpty()) ipAddress = request.getRemoteAddr();
    return ipAddress;
  }

  public static String getAuthTokenFromRequest(HttpServletRequest request) {
    return request.getHeader(AUTH_HEADER);
  }
}
