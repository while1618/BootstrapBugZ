package org.bootstrapbugz.api.auth.service;

import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;

public interface JwtService {
  String createToken(String username, JwtPurpose purpose);

  void checkToken(String token, JwtPurpose purpose);

  void invalidateToken(String token);

  void invalidateAllTokens(String username);

  String createRefreshToken(String username);

  void checkRefreshToken(String refreshToken);

  String findRefreshToken(String username);

  void deleteRefreshToken(String token);
}
