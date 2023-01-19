package org.bootstrapbugz.api.auth.jwt.service;

import java.util.Set;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;

public interface AccessTokenService {
  String create(Long userId, Set<RoleDTO> roleDTOs);

  void check(String token);

  void invalidate(String token);

  void invalidateAllByUser(Long userId);
}
