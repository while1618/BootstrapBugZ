package org.bootstrapbugz.api.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role.RoleName;
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
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    final var userPrincipal = (UserPrincipal) auth.getPrincipal();
    return userPrincipal.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(RoleName.ADMIN.name()));
  }

  public static UserDTO findSignedInUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return UserMapper.INSTANCE.userPrincipalToUserDTO((UserPrincipal) auth.getPrincipal());
  }

  public static String getUserIpAddress(HttpServletRequest request) {
    final var ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.isEmpty()) return request.getRemoteAddr();
    return ipAddress;
  }

  public static String getAccessTokenFromRequest(HttpServletRequest request) {
    return request.getHeader(AUTH_HEADER);
  }
}
