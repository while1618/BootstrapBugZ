package org.bugzkit.api.auth.jwt.service;

import java.util.Optional;
import java.util.Set;
import org.bugzkit.api.user.payload.dto.RoleDTO;

public interface RefreshTokenService {
  String create(Long userId, Set<RoleDTO> roleDTOs, String ipAddress);

  void check(String token);

  Optional<String> findByUserIdAndIpAddress(Long userId, String ipAddress);

  void delete(String token);

  void deleteByUserIdAndIpAddress(Long userId, String ipAddress);

  void deleteAllByUserId(Long userId);
}
