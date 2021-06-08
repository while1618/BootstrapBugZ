package org.bootstrapbugz.api.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtUtil {
  private JwtUtil() {}

  public static final String HEADER = "Authorization";
  public static final String BEARER = "Bearer ";

  public enum JwtPurpose {
    ACCESSING_RESOURCES,
    REFRESH_TOKEN,
    CONFIRM_REGISTRATION,
    FORGOT_PASSWORD
  }

  public static String createToken(String username, int expirationTimeInSecs, String secret) {
    return JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
        .withIssuedAt(new Date())
        .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static String createRefreshToken(
      String username, int expirationTimeInSecs, String secret) {
    return JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
        .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static void isValid(String token, String secret) {
    JWT.require(Algorithm.HMAC512(secret.getBytes())).build().verify(token);
  }

  public static String getSubject(String token) {
    return JWT.decode(removeBearerFromToken(token)).getSubject();
  }

  public static String removeBearerFromToken(String token) {
    return token.replace(BEARER, "");
  }
}
