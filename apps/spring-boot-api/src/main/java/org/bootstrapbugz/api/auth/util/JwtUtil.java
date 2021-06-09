package org.bootstrapbugz.api.auth.util;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtUtil {
  public static final String AUTH_HEADER = "Authorization";
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
            .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSecs * 1000L))
            .withIssuedAt(new Date())
            .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  public static void isTokenValid(String token, String secret) {
    JWT.require(Algorithm.HMAC512(secret.getBytes())).build().verify(token);
  }

  public static String removeTokenTypeFromToken(String token) {
    return token.replace(TOKEN_TYPE, "");
  }
}
