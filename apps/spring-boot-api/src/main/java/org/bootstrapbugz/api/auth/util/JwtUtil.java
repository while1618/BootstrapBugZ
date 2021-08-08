package org.bootstrapbugz.api.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;

public class JwtUtil {
  public static final String TOKEN_TYPE = "Bearer ";

  private JwtUtil() {}

  public static String createToken(Long userId, int expirationTimeInSecs, String secret) {
    return JWT.create()
        .withClaim("userId", userId)
        .withClaim("issuedAt", Instant.now().toString())
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
        .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static String createToken(
      Long userId, Set<Role> roles, int expirationTimeInSecs, String secret) {
    return JWT.create()
        .withClaim("userId", userId)
        .withClaim("issuedAt", Instant.now().toString())
        .withClaim("roles", roles.stream().map(role -> role.getName().name()).toList())
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
        .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static void isTokenValid(String token, String secret) {
    JWT.require(Algorithm.HMAC512(secret.getBytes())).build().verify(token);
  }

  public static String removeTokenTypeFromToken(String token) {
    return token.replace(TOKEN_TYPE, "");
  }

  public static Long getUserId(String token) {
    return JWT.decode(token).getClaim("userId").asLong();
  }

  public static List<String> getRoles(String token) {
    return JWT.decode(token).getClaim("roles").asList(String.class);
  }

  public static String getIssuedAt(String token) {
    return JWT.decode(token).getClaim("issuedAt").asString();
  }

  public enum JwtPurpose {
    ACCESSING_RESOURCES,
    REFRESH_TOKEN,
    CONFIRM_REGISTRATION,
    FORGOT_PASSWORD
  }
}
