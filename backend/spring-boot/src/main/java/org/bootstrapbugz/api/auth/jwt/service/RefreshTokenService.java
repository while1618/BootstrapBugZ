package org.bootstrapbugz.api.auth.jwt.service;

import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;

public interface RefreshTokenService {
  String create(Long userId, Set<RoleDTO> roleDTOs, String ipAddress);

  void check(String token);

  Optional<String> findByUserAndIpAddress(Long userId, String ipAddress);

  void delete(String token);

  void deleteByUserAndIpAddress(Long userId, String ipAddress);

  void deleteAllByUser(Long userId);
}
