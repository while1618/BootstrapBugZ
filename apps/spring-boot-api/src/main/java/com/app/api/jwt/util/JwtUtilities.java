package com.app.api.jwt.util;

import com.app.api.user.model.User;
import com.app.api.user.security.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtilities {
  public static final String HEADER = "Authorization";
  public static final String BEARER = "Bearer ";
  private static final int EXPIRATION_TIME = 3600000; // 1h

  @Value("${jwt.serverSecret}")
  private String serverSecret;

  public String createToken(User user, JwtPurpose purpose) {
    String secret = createSecret(user.getUpdatedAt(), user.getLastLogout(), purpose);
    return createToken(user.getUsername(), secret);
  }

  public void checkToken(String token, User user, JwtPurpose purpose) {
    String secret = createSecret(user.getUpdatedAt(), user.getLastLogout(), purpose);
    checkToken(token, secret);
  }

  public String createToken(UserPrincipal userPrincipal, JwtPurpose purpose) {
    String secret =
        createSecret(userPrincipal.getUpdatedAt(), userPrincipal.getLastLogout(), purpose);
    return createToken(userPrincipal.getUsername(), secret);
  }

  public void checkToken(String token, UserPrincipal userPrincipal, JwtPurpose purpose) {
    String secret =
        createSecret(userPrincipal.getUpdatedAt(), userPrincipal.getLastLogout(), purpose);
    checkToken(token, secret);
  }

  private String createSecret(
      LocalDateTime updatedAt, LocalDateTime lastLogout, JwtPurpose purpose) {
    return serverSecret
        + "."
        + updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)
        + "."
        + lastLogout.format(DateTimeFormatter.ISO_DATE_TIME)
        + "."
        + purpose;
  }

  private String createToken(String username, String secret) {
    return JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .sign(Algorithm.HMAC512(secret.getBytes()));
  }

  private void checkToken(String token, String secret) {
    JWT.require(Algorithm.HMAC512(secret.getBytes())).build().verify(token.replace(BEARER, ""));
  }

  public String getSubject(String token) {
    return JWT.decode(token.replace(BEARER, "")).getSubject();
  }
}
