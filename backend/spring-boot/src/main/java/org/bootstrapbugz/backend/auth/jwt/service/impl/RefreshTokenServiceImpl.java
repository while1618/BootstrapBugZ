package org.bootstrapbugz.backend.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bootstrapbugz.backend.auth.jwt.redis.model.RefreshTokenStore;
import org.bootstrapbugz.backend.auth.jwt.redis.repository.RefreshTokenStoreRepository;
import org.bootstrapbugz.backend.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.backend.shared.error.exception.BadRequestException;
import org.bootstrapbugz.backend.user.payload.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private static final JwtPurpose PURPOSE = JwtPurpose.REFRESH_TOKEN;
  private final RefreshTokenStoreRepository refreshTokenStoreRepository;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.refresh-token.duration}")
  private int tokenDuration;

  public RefreshTokenServiceImpl(RefreshTokenStoreRepository refreshTokenStoreRepository) {
    this.refreshTokenStoreRepository = refreshTokenStoreRepository;
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
    verifyToken(token);
    isInRefreshTokenStore(token);
  }

  private void verifyToken(String token) {
    try {
      JwtUtil.verify(token, secret, PURPOSE);
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
      throw new BadRequestException("auth.tokenInvalid");
    }
  }

  private void isInRefreshTokenStore(String token) {
    if (!refreshTokenStoreRepository.existsById(token))
      throw new BadRequestException("auth.tokenInvalid");
  }

  @Override
  public Optional<String> findByUserIdAndIpAddress(Long userId, String ipAddress) {
    return refreshTokenStoreRepository
        .findByUserIdAndIpAddress(userId, ipAddress)
        .map(RefreshTokenStore::getRefreshToken);
  }

  @Override
  public void delete(String token) {
    refreshTokenStoreRepository.deleteById(token);
  }

  @Override
  public void deleteByUserIdAndIpAddress(Long userId, String ipAddress) {
    refreshTokenStoreRepository
        .findByUserIdAndIpAddress(userId, ipAddress)
        .ifPresent(refreshTokenStoreRepository::delete);
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    final var refreshTokens = refreshTokenStoreRepository.findAllByUserId(userId);
    refreshTokenStoreRepository.deleteAll(refreshTokens);
  }
}
