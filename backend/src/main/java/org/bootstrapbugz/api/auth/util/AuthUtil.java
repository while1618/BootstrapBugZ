package org.bootstrapbugz.api.auth.util;

import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.service.RoleService;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  public static final String AUTH_HEADER = "Authorization";

  private AuthUtil() {}

  public static boolean isSignedIn() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return !auth.getPrincipal().equals("anonymousUser");
  }

  public static boolean isAdminSignedIn(RoleService roleService) {
    if (!isSignedIn()) return false;
    User user = findSignedInUser(roleService);
    return user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ADMIN));
  }

  public static User findSignedInUser(RoleService roleService) {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return userPrincipalToUser((UserPrincipal) auth.getPrincipal(), roleService);
  }

  public static User userPrincipalToUser(UserPrincipal userPrincipal, RoleService roleService) {
    final var roleNames =
        userPrincipal.getAuthorities().stream()
            .map(authority -> RoleName.valueOf(authority.getAuthority()))
            .collect(Collectors.toSet());
    final var roles = roleService.findAllByNameIn(roleNames);
    return new User()
        .setId(userPrincipal.getId())
        .setFirstName(userPrincipal.getFirstName())
        .setLastName(userPrincipal.getLastName())
        .setUsername(userPrincipal.getUsername())
        .setEmail(userPrincipal.getEmail())
        .setPassword(userPrincipal.getPassword())
        .setActivated(userPrincipal.isEnabled())
        .setNonLocked(userPrincipal.isAccountNonLocked())
        .setRoles(Set.copyOf(roles));
  }

  public static String getUserIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.isEmpty()) ipAddress = request.getRemoteAddr();
    return ipAddress;
  }

  public static String getAccessTokenFromRequest(HttpServletRequest request) {
    return request.getHeader(AUTH_HEADER);
  }
}
