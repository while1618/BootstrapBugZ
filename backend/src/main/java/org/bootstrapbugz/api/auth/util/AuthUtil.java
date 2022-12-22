package org.bootstrapbugz.api.auth.util;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDto;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  public static final String AUTH_HEADER = "Authorization";

  private AuthUtil() {}

  public static boolean isSignedIn() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return !auth.getPrincipal().equals("anonymousUser");
  }

  public static boolean isAdminSignedIn() {
    if (!isSignedIn()) return false;
    var userDto = findSignedInUser();
    return userDto.getRoles().stream()
        .anyMatch(roleDto -> roleDto.getName().equals(RoleName.ADMIN.name()));
  }

  public static UserDto findSignedInUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return userPrincipalToUserDto((UserPrincipal) auth.getPrincipal());
  }

  public static UserDto userPrincipalToUserDto(UserPrincipal userPrincipal) {
    final var roleNames =
        userPrincipal.getAuthorities().stream()
            .map(authority -> new RoleDto(RoleName.valueOf(authority.getAuthority()).name()))
            .collect(Collectors.toSet());
    return new UserDto()
        .setId(userPrincipal.getId())
        .setFirstName(userPrincipal.getFirstName())
        .setLastName(userPrincipal.getLastName())
        .setUsername(userPrincipal.getUsername())
        .setEmail(userPrincipal.getEmail())
        .setActivated(userPrincipal.isEnabled())
        .setNonLocked(userPrincipal.isAccountNonLocked())
        .setRoles(roleNames);
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
