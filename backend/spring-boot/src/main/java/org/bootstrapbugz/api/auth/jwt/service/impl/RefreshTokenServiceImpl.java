package org.bootstrapbugz.api.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenStoreRepository;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private static final JwtPurpose PURPOSE = JwtPurpose.REFRESH_TOKEN;
  private final RefreshTokenStoreRepository refreshTokenStoreRepository;
  private final MessageService messageService;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.refresh-token.duration}")
  private int tokenDuration;

  public RefreshTokenServiceImpl(
      RefreshTokenStoreRepository refreshTokenStoreRepository, MessageService messageService) {
    this.refreshTokenStoreRepository = refreshTokenStoreRepository;
    this.messageService = messageService;
  }

  @Override
  public String create(Long userId, Set<RoleDTO> roleDTOs, String ipAddress) {
    final var token =
        JWT.create()
            .withIssuer(userId.toString())
            .withClaim("roles", roleDTOs.stream().map(RoleDTO::name).toList())
            .withClaim("purpose", PURPOSE.name())
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusSeconds(tokenDuration))
            .sign(JwtUtil.getAlgorithm(secret));
    refreshTokenStoreRepository.save(
        new RefreshTokenStore(token, userId, ipAddress, tokenDuration));
    return token;
  }

  @Override
  public void check(String token) {
    try {
      JwtUtil.verify(token, secret, PURPOSE);
    } catch (RuntimeException e) {
      throw new BadRequestException("token", e.getMessage());
    }
    isInRefreshTokenStore(token);
  }

  private void isInRefreshTokenStore(String token) {
    if (!refreshTokenStoreRepository.existsById(token))
      throw new BadRequestException("token", messageService.getMessage("token.invalid"));
  }

  @Override
  public Optional<String> findByUserIdAndIpAddress(Long userId, String ipAddress) {
    final var refreshTokenStore =
        refreshTokenStoreRepository.findByUserIdAndIpAddress(userId, ipAddress);
    return refreshTokenStore.map(RefreshTokenStore::getRefreshToken);
  }

  @Override
  public void delete(String token) {
    refreshTokenStoreRepository.deleteById(token);
  }

  @Override
  public void deleteByUserIdAndIpAddress(Long userId, String ipAddress) {
    final var token = refreshTokenStoreRepository.findByUserIdAndIpAddress(userId, ipAddress);
    token.ifPresent(refreshTokenStoreRepository::delete);
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    final var refreshTokens = refreshTokenStoreRepository.findAllByUserId(userId);
    refreshTokenStoreRepository.deleteAll(refreshTokens);
  }
}
