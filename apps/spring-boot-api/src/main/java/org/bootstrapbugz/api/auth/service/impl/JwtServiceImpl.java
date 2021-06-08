package org.bootstrapbugz.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.Optional;
import org.bootstrapbugz.api.auth.redis.model.JwtBlacklist;
import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.redis.repository.JwtBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
import org.bootstrapbugz.api.auth.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.expirationTimeInSecs}")
  private int expirationTimeInSecs; // 15min

  @Value("${refreshToken.expirationTimeInSecs}")
  private int refreshTokenExpirationTimeInSecs; // 7 days

  @Value("${jwt.serverSecret}")
  private String serverSecret;

  private final JwtBlacklistRepository jwtBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final MessageSource messageSource;

  public JwtServiceImpl(
      JwtBlacklistRepository jwtBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository,
      RefreshTokenRepository refreshTokenRepository,
      MessageSource messageSource) {
    this.jwtBlacklistRepository = jwtBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.messageSource = messageSource;
  }

  private String createSecret(JwtPurpose purpose) {
    return serverSecret + "." + purpose;
  }

  @Override
  public String createToken(String username, JwtPurpose purpose) {
    return JwtUtil.createToken(username, expirationTimeInSecs, createSecret(purpose));
  }

  @Override
  public void checkToken(String token, JwtPurpose purpose) {
    JwtUtil.isTokenValid(token, createSecret(purpose));
    isInJwtBlacklist(token);
    isInUserBlacklist(token);
  }

  private void isInJwtBlacklist(String token) {
    if (jwtBlacklistRepository.existsById(token))
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

  @Override
  public void invalidateToken(String token) {
    jwtBlacklistRepository.save(new JwtBlacklist(token));
  }

  @Override
  public void invalidateAllTokens(String username) {
    userBlacklistRepository.save(new UserBlacklist(username, new Date()));
  }

  @Override
  public String createRefreshToken(String username) {
    String refreshToken =
        JwtUtil.createToken(
            username, refreshTokenExpirationTimeInSecs, createSecret(JwtPurpose.REFRESH_TOKEN));
    refreshTokenRepository.save(new RefreshToken(refreshToken));
    return refreshToken;
  }

  @Override
  public void checkRefreshToken(String refreshToken) {
    JwtUtil.isTokenValid(refreshToken, createSecret(JwtPurpose.REFRESH_TOKEN));
    if (!refreshTokenRepository.existsById(refreshToken))
      throw new ForbiddenException(
          messageSource.getMessage("token.invalid", null, LocaleContextHolder.getLocale()),
          ErrorDomain.AUTH);
  }

  @Override
  public void deleteRefreshToken(String token) {
    refreshTokenRepository.deleteById(token);
  }
}
