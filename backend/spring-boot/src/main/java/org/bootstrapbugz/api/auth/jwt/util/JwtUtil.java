package org.bootstrapbugz.api.auth.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.util.Set;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;

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
    return Long.parseLong(JWT.decode(token).getIssuer());
  }

  public static Set<RoleDTO> getRoleDTOs(String token) {
    return Set.copyOf(JWT.decode(token).getClaim("roles").asList(RoleDTO.class));
  }

  public static Instant getIssuedAt(String token) {
    return Instant.ofEpochMilli(JWT.decode(token).getClaim("issuedAt").asLong());
  }

  public enum JwtPurpose {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    CONFIRM_REGISTRATION_TOKEN,
    RESET_PASSWORD_TOKEN
  }
}
