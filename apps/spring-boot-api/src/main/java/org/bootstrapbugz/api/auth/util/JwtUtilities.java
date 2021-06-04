package org.bootstrapbugz.api.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.Optional;
import org.bootstrapbugz.api.auth.model.JwtBlacklist;
import org.bootstrapbugz.api.auth.repository.JwtBlacklistRepository;
import org.bootstrapbugz.api.auth.model.UserBlacklist;
import org.bootstrapbugz.api.auth.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtilities {
  public static final String HEADER = "Authorization";
  public static final String BEARER = "Bearer ";

  @Value("${jwt.expirationTime}")
  private int expirationTime; // 1h

  @Value("${jwt.serverSecret}")
  private String serverSecret;

  private final JwtBlacklistRepository jwtBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;
  private final MessageSource messageSource;

  public JwtUtilities(
      JwtBlacklistRepository jwtBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository,
      MessageSource messageSource) {
    this.jwtBlacklistRepository = jwtBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
    this.messageSource = messageSource;
  }

  private String createSecret(JwtPurpose purpose) {
    return serverSecret + "." + purpose;
  }

  public String createToken(String username, JwtPurpose purpose) {
    return JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
        .withIssuedAt(new Date())
        .sign(Algorithm.HMAC512(createSecret(purpose).getBytes()));
  }

  public void checkToken(String token, JwtPurpose purpose) {
    token = token.replace(BEARER, "");
    isValid(token, purpose);
    isInJwtBlacklist(token);
    isInUserBlacklist(token);
  }

  private void isValid(String token, JwtPurpose purpose) {
    JWT.require(Algorithm.HMAC512(createSecret(purpose).getBytes())).build().verify(token);
  }

  private void isInJwtBlacklist(String token) {
    Optional<JwtBlacklist> tokenInBlacklist = jwtBlacklistRepository.findById(token);
    if (tokenInBlacklist.isPresent())
      throw new ForbiddenException(
          messageSource.getMessage("token.invalid", null, LocaleContextHolder.getLocale()),
          ErrorDomain.AUTH);
  }

  private void isInUserBlacklist(String token) {
    DecodedJWT decodedJwt = JWT.decode(token);
    Optional<UserBlacklist> userInBlacklist =
        userBlacklistRepository.findById(decodedJwt.getSubject());
    if (userInBlacklist.isPresent()
        && decodedJwt.getIssuedAt().before(userInBlacklist.get().getUpdatedAt()))
      throw new ForbiddenException(
          messageSource.getMessage("token.invalid", null, LocaleContextHolder.getLocale()),
          ErrorDomain.AUTH);
  }

  public String getSubject(String token) {
    return JWT.decode(token.replace(BEARER, "")).getSubject();
  }
}
