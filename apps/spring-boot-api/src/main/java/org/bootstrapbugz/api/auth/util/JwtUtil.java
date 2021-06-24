package org.bootstrapbugz.api.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {
  public static final String TOKEN_TYPE = "Bearer ";

  private JwtUtil() {}

  public enum JwtPurpose {
    ACCESSING_RESOURCES,
    REFRESH_TOKEN,
    CONFIRM_REGISTRATION,
    FORGOT_PASSWORD
  }

  public static String createToken(String username, int expirationTimeInSecs, String secret) {
    return TOKEN_TYPE
        + JWT.create()
            .withSubject(username)
            .withClaim("issuedAt", Instant.now().toString())
            .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
            .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static void isTokenValid(String token, String secret) {
    JWT.require(Algorithm.HMAC512(secret.getBytes())).build().verify(token);
  }

  public static String removeTokenTypeFromToken(String token) {
    return token.replace(TOKEN_TYPE, "");
  }
}
