package org.bootstrapbugz.api.auth.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.service.RoleService;

public class JwtUtil {
  private static final String TOKEN_TYPE = "Bearer ";

  private JwtUtil() {}

  public static void verify(String token, String secret, JwtPurpose purpose) {
    JWT.require(JwtUtil.getAlgorithm(secret))
        .withClaim("purpose", purpose.name())
        .build()
        .verify(token);
  }

  public static Algorithm getAlgorithm(String secret) {
    return Algorithm.HMAC512(secret.getBytes());
  }

  public static String addBearer(String token) {
    return TOKEN_TYPE + token;
  }

  public static String removeBearer(String token) {
    return token.replace(TOKEN_TYPE, "");
  }

  public static boolean isBearer(String token) {
    return token.startsWith(JwtUtil.TOKEN_TYPE);
  }

  public static Long getUserId(String token) {
    return JWT.decode(token).getClaim("userId").asLong();
  }

  public static Set<Role> getRoles(String token, RoleService roleService) {
    final var roleNames = Set.copyOf(JWT.decode(token).getClaim("roles").asList(RoleName.class));
    return Set.copyOf(roleService.findAllByNameIn(roleNames));
  }

  public static String getIssuedAt(String token) {
    return JWT.decode(token).getClaim("issuedAt").asString();
  }

  public enum JwtPurpose {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    CONFIRM_REGISTRATION_TOKEN,
    FORGOT_PASSWORD_TOKEN
  }
}
