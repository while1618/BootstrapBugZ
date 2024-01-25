package org.bootstrapbugz.api.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.redis.model.AccessTokenBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {
  private static final JwtPurpose PURPOSE = JwtPurpose.ACCESS_TOKEN;
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final UserBlacklistRepository userBlacklistRepository;
  private final MessageService messageService;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token.duration}")
  private int tokenDuration;

  public AccessTokenServiceImpl(
      AccessTokenBlacklistRepository accessTokenBlacklistRepository,
      UserBlacklistRepository userBlacklistRepository,
      MessageService messageService) {
    this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
    this.userBlacklistRepository = userBlacklistRepository;
    this.messageService = messageService;
  }

  @Override
  public String create(Long userId, Set<RoleDTO> roleDTOs) {
    return JWT.create()
        .withIssuer(userId.toString())
        .withClaim("roles", roleDTOs.stream().map(RoleDTO::getName).toList())
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
      throw new UnauthorizedException(e.getMessage());
    }
  }

  private void isInAccessTokenBlacklist(String token) {
    if (accessTokenBlacklistRepository.existsById(token))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"));
  }

  private void isInUserBlacklist(String token) {
    final var userId = JwtUtil.getUserId(token);
    final var issuedAt = JwtUtil.getIssuedAt(token);
    final var userInBlacklist = userBlacklistRepository.findById(userId);
    if (userInBlacklist.isEmpty()) return;
    if (issuedAt.isBefore(userInBlacklist.get().getUpdatedAt())
        || issuedAt.equals(userInBlacklist.get().getUpdatedAt()))
      throw new UnauthorizedException(messageService.getMessage("token.invalid"));
  }

  @Override
  public void invalidate(String token) {
    accessTokenBlacklistRepository.save(new AccessTokenBlacklist(token, tokenDuration));
  }

  @Override
  public void invalidateAllByUserId(Long userId) {
    var userBlacklist =
        new UserBlacklist(userId, tokenDuration, Instant.now().truncatedTo(ChronoUnit.SECONDS));
    userBlacklistRepository.save(userBlacklist);
  }
}
