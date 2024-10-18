package org.bootstrapbugz.backend.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.jwt.redis.model.AccessTokenBlacklist;
import org.bootstrapbugz.backend.auth.jwt.redis.model.UserBlacklist;
import org.bootstrapbugz.backend.auth.jwt.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.backend.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.backend.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.backend.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.backend.user.payload.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccessTokenServiceImpl implements AccessTokenService {
  private static final JwtPurpose PURPOSE = JwtPurpose.ACCESS_TOKEN;
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token.duration}")
  private int tokenDuration;

  public AccessTokenServiceImpl(
      AccessTokenBlacklistRepository accessTokenBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository) {
    this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
  }

  @Override
  public String create(Long userId, Set<RoleDTO> roleDTOs) {
    return JWT.create()
        .withIssuer(userId.toString())
        .withClaim("roles", roleDTOs.stream().map(RoleDTO::name).toList())
        .withClaim("purpose", PURPOSE.name())
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plusSeconds(tokenDuration))
        .sign(JwtUtil.getAlgorithm(secret));
  }

  @Override
  public void check(String token) {
    verifyToken(token);
    isInAccessTokenBlacklist(token);
    isInUserBlacklist(token);
  }

  private void verifyToken(String token) {
    try {
      JwtUtil.verify(token, secret, PURPOSE);
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
      throw new UnauthorizedException("auth.tokenInvalid");
    }
  }

  private void isInAccessTokenBlacklist(String token) {
    if (accessTokenBlacklistRepository.existsById(token))
      throw new UnauthorizedException("auth.tokenInvalid");
  }

  private void isInUserBlacklist(String token) {
    final var userId = JwtUtil.getUserId(token);
    final var issuedAt = JwtUtil.getIssuedAt(token);
    final var userInBlacklist = userBlacklistRepository.findById(userId);
    if (userInBlacklist.isEmpty()) return;
    if (issuedAt.isBefore(userInBlacklist.get().getUpdatedAt())
        || issuedAt.equals(userInBlacklist.get().getUpdatedAt()))
      throw new UnauthorizedException("auth.tokenInvalid");
  }

  @Override
  public void invalidate(String token) {
    accessTokenBlacklistRepository.save(new AccessTokenBlacklist(token, tokenDuration));
  }

  @Override
  public void invalidateAllByUserId(Long userId) {
    userBlacklistRepository.save(
        UserBlacklist.builder().userId(userId).timeToLive(tokenDuration).build());
  }
}
