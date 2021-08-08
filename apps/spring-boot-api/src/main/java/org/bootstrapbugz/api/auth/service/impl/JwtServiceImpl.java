package org.bootstrapbugz.api.auth.service.impl;

import java.time.Instant;
import java.util.Set;
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
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
  private final JwtBlacklistRepository jwtBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final MessageService messageService;

  @Value("${jwt.expiration-time-in-secs}")
  private int expirationTimeInSecs; // 15min

  @Value("${refresh-token.expiration-time-in-secs}")
  private int refreshTokenExpirationTimeInSecs; // 7 days

  @Value("${jwt.server-secret}")
  private String serverSecret;

  public JwtServiceImpl(
      JwtBlacklistRepository jwtBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository,
      RefreshTokenRepository refreshTokenRepository,
      MessageService messageService) {
    this.jwtBlacklistRepository = jwtBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.messageService = messageService;
  }

  private String createSecret(JwtPurpose purpose) {
    return serverSecret + "." + purpose;
  }

  @Override
  public String createToken(Long userId, JwtPurpose purpose) {
    return JwtUtil.createToken(userId, expirationTimeInSecs, createSecret(purpose));
  }

  @Override
  public String createToken(Long userId, Set<Role> roles, JwtPurpose purpose) {
    return JwtUtil.createToken(userId, roles, expirationTimeInSecs, createSecret(purpose));
  }

  @Override
  public void checkToken(String token, JwtPurpose purpose) {
    JwtUtil.isTokenValid(token, createSecret(purpose));
    isInJwtBlacklist(token);
    isInUserBlacklist(token);
  }

  private void isInJwtBlacklist(String token) {
    if (jwtBlacklistRepository.existsById(token))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"), ErrorDomain.AUTH);
  }

  private void isInUserBlacklist(String token) {
    final var userInBlacklist = userBlacklistRepository.findById(JwtUtil.getUserId(token));
    if (userInBlacklist.isPresent()
        && Instant.parse(JwtUtil.getIssuedAt(token)).isBefore(userInBlacklist.get().getUpdatedAt()))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"), ErrorDomain.AUTH);
  }

  @Override
  public void invalidateToken(String token) {
    jwtBlacklistRepository.save(new JwtBlacklist(token, expirationTimeInSecs));
  }

  @Override
  public void invalidateAllTokens(Long userId) {
    userBlacklistRepository.save(new UserBlacklist(userId, Instant.now(), expirationTimeInSecs));
  }

  @Override
  public String createRefreshToken(Long userId, Set<Role> roles, String ipAddress) {
    final String refreshToken =
        JwtUtil.createToken(
            userId,
            roles,
            refreshTokenExpirationTimeInSecs,
            createSecret(JwtPurpose.REFRESH_TOKEN));
    refreshTokenRepository.save(
        new RefreshToken(refreshToken, userId, ipAddress, refreshTokenExpirationTimeInSecs));
    return refreshToken;
  }

  @Override
  public void checkRefreshToken(String refreshToken) {
    JwtUtil.isTokenValid(refreshToken, createSecret(JwtPurpose.REFRESH_TOKEN));
    if (!refreshTokenRepository.existsById(refreshToken))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"), ErrorDomain.AUTH);
  }

  @Override
  public String findRefreshToken(Long userId, String ipAddress) {
    final var refreshToken = refreshTokenRepository.findByUserIdAndIpAddress(userId, ipAddress);
    return refreshToken.map(RefreshToken::getToken).orElse(null);
  }

  @Override
  public void deleteRefreshToken(String token) {
    refreshTokenRepository.deleteById(token);
  }

  @Override
  public void deleteRefreshTokenByUserAndIpAddress(Long userId, String ipAddress) {
    var refreshToken = refreshTokenRepository.findByUserIdAndIpAddress(userId, ipAddress);
    refreshToken.ifPresent(refreshTokenRepository::delete);
  }

  @Override
  public void deleteAllRefreshTokensByUser(Long userId) {
    var refreshTokens = refreshTokenRepository.findAllByUserId(userId);
    refreshTokenRepository.deleteAll(refreshTokens);
  }
}
