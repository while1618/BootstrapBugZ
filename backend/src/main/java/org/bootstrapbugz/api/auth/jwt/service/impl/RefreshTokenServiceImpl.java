package org.bootstrapbugz.api.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.payload.dto.RoleDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private static final JwtUtil.JwtPurpose PURPOSE = JwtUtil.JwtPurpose.REFRESH_TOKEN;
  private final RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  private final MessageService messageService;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.refresh-token.duration}")
  private int tokenDuration;

  public RefreshTokenServiceImpl(
      RefreshTokenWhitelistRepository refreshTokenWhitelistRepository,
      MessageService messageService) {
    this.refreshTokenWhitelistRepository = refreshTokenWhitelistRepository;
    this.messageService = messageService;
  }

  @Override
  public String create(Long userId, Set<RoleDto> roleDtos, String ipAddress) {
    final var token =
        JWT.create()
            .withClaim("userId", userId)
            .withClaim("issuedAt", Instant.now().toString())
            .withClaim("roles", roleDtos.stream().map(RoleDto::getName).toList())
            .withClaim("purpose", PURPOSE.name())
            .withExpiresAt(new Date(System.currentTimeMillis() + tokenDuration * 1000L))
            .sign(JwtUtil.getAlgorithm(secret));
    refreshTokenWhitelistRepository.save(
        new RefreshTokenWhitelist(token, userId, ipAddress, tokenDuration));
    return token;
  }

  @Override
  public void check(String token) {
    JwtUtil.verify(token, secret, PURPOSE);
    isNotInRefreshTokenWhitelist(token);
  }

  private void isNotInRefreshTokenWhitelist(String token) {
    if (!refreshTokenWhitelistRepository.existsById(token))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"));
  }

  @Override
  public String findByUserAndIpAddress(Long userId, String ipAddress) {
    final var refreshToken =
        refreshTokenWhitelistRepository.findByUserIdAndIpAddress(userId, ipAddress);
    return refreshToken.map(RefreshTokenWhitelist::getRefreshToken).orElse(null);
  }

  @Override
  public void delete(String token) {
    refreshTokenWhitelistRepository.deleteById(token);
  }

  @Override
  public void deleteByUserAndIpAddress(Long userId, String ipAddress) {
    final var token = refreshTokenWhitelistRepository.findByUserIdAndIpAddress(userId, ipAddress);
    token.ifPresent(refreshTokenWhitelistRepository::delete);
  }

  @Override
  public void deleteAllByUser(Long userId) {
    final var refreshTokens = refreshTokenWhitelistRepository.findAllByUserId(userId);
    refreshTokenWhitelistRepository.deleteAll(refreshTokens);
  }
}
