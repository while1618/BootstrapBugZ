package org.bootstrapbugz.api.auth.service;

import java.util.Set;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.user.model.Role;

public interface JwtService {
  String createToken(Long userId, Set<Role> roles, JwtPurpose purpose);

  String createToken(Long userId, JwtPurpose purpose);

  void checkToken(String token, JwtPurpose purpose);

  void invalidateToken(String token);

  void invalidateAllTokens(Long userId);

  String createRefreshToken(Long userId, Set<Role> roles, String ipAddress);

  void checkRefreshToken(String refreshToken);

  String findRefreshToken(Long userId, String ipAddress);

  void deleteRefreshToken(String token);

  void deleteRefreshTokenByUserAndIpAddress(Long userId, String ipAddress);

  void deleteAllRefreshTokensByUser(Long userId);
}
