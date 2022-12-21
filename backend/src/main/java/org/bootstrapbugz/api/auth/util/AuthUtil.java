package org.bootstrapbugz.api.auth.util;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
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
    var userDTO = findSignedInUser();
    return userDTO.getRoles().stream()
        .anyMatch(roleDTO -> roleDTO.getName().equals(RoleName.ADMIN.name()));
  }

  public static UserDTO findSignedInUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return userPrincipalToUserDTO((UserPrincipal) auth.getPrincipal());
  }

  public static UserDTO userPrincipalToUserDTO(UserPrincipal userPrincipal) {
    final var roleNames =
        userPrincipal.getAuthorities().stream()
            .map(authority -> new RoleDTO(RoleName.valueOf(authority.getAuthority()).name()))
            .collect(Collectors.toSet());
    return new UserDTO()
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
