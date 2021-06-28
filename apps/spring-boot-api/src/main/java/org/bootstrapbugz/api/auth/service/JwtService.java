package org.bootstrapbugz.api.auth.service;

import java.util.Optional;

import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;

public interface JwtService {
  String createToken(String username, JwtPurpose purpose);

  void checkToken(String token, JwtPurpose purpose);

  void invalidateToken(String token);

  void invalidateAllTokens(String username);

  String createRefreshToken(String username, String ipAddress);

  void checkRefreshToken(String refreshToken);

  Optional<RefreshToken> findRefreshToken(String username, String ipAddress);

  void deleteRefreshToken(String token);

  void deleteRefreshTokenByUserAndIpAddress(String username, String ipAddress);

  void deleteAllRefreshTokensByUser(String username);
}
