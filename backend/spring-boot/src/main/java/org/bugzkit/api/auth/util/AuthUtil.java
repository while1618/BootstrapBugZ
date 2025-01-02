package org.bugzkit.api.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.bugzkit.api.auth.security.UserPrincipal;
import org.bugzkit.api.user.model.Role.RoleName;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
  private AuthUtil() {}

  public static boolean isSignedIn() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return !auth.getPrincipal().equals("anonymousUser");
  }

  public static boolean isAdminSignedIn() {
    if (!isSignedIn()) return false;
    final var userPrincipal = findSignedInUser();
    return userPrincipal.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(RoleName.ADMIN.name()));
  }

  public static UserPrincipal findSignedInUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    return (UserPrincipal) auth.getPrincipal();
  }

  public static String getAuthName() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal().equals("anonymousUser")) return "anonymousUser";
    return auth.getName();
  }

  public static String getUserIpAddress(HttpServletRequest request) {
    final var ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.isEmpty()) return request.getRemoteAddr();
    return ipAddress;
  }

  public static String getAccessTokenFromRequest(HttpServletRequest request) {
    return request.getHeader(HttpHeaders.AUTHORIZATION);
  }
}
