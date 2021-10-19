package org.bootstrapbugz.api.auth.service.impl;

import org.bootstrapbugz.api.auth.redis.model.AccessTokenBlacklist;
import org.bootstrapbugz.api.auth.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenWhitelistRepository;
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

import java.time.Instant;
import java.util.Set;

@Service
public class JwtServiceImpl implements JwtService {
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;
  private final RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  private final MessageService messageService;

  @Value("${jwt.expiration-time-in-secs}")
  private int expirationTimeInSecs; // 15min

  @Value("${refresh-token.expiration-time-in-secs}")
  private int refreshTokenExpirationTimeInSecs; // 7 days

  @Value("${jwt.server-secret}")
  private String serverSecret;

  public JwtServiceImpl(
      AccessTokenBlacklistRepository accessTokenBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository,
      RefreshTokenWhitelistRepository refreshTokenWhitelistRepository,
      MessageService messageService) {
    this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
    this.refreshTokenWhitelistRepository = refreshTokenWhitelistRepository;
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
    if (accessTokenBlacklistRepository.existsById(token))
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
    accessTokenBlacklistRepository.save(new AccessTokenBlacklist(token, expirationTimeInSecs));
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
    refreshTokenWhitelistRepository.save(
        new RefreshTokenWhitelist(
            refreshToken, userId, ipAddress, refreshTokenExpirationTimeInSecs));
    return refreshToken;
  }

  @Override
  public void checkRefreshToken(String refreshToken) {
    JwtUtil.isTokenValid(refreshToken, createSecret(JwtPurpose.REFRESH_TOKEN));
    if (!refreshTokenWhitelistRepository.existsById(refreshToken))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"), ErrorDomain.AUTH);
  }

  @Override
  public String findRefreshToken(Long userId, String ipAddress) {
    final var refreshToken =
        refreshTokenWhitelistRepository.findByUserIdAndIpAddress(userId, ipAddress);
    return refreshToken.map(RefreshTokenWhitelist::getRefreshToken).orElse(null);
  }

  @Override
  public void deleteRefreshToken(String token) {
    refreshTokenWhitelistRepository.deleteById(token);
  }

  @Override
  public void deleteRefreshTokenByUserAndIpAddress(Long userId, String ipAddress) {
    var refreshToken = refreshTokenWhitelistRepository.findByUserIdAndIpAddress(userId, ipAddress);
    refreshToken.ifPresent(refreshTokenWhitelistRepository::delete);
  }

  @Override
  public void deleteAllRefreshTokensByUser(Long userId) {
    var refreshTokens = refreshTokenWhitelistRepository.findAllByUserId(userId);
    refreshTokenWhitelistRepository.deleteAll(refreshTokens);
  }
}
