package org.bootstrapbugz.api.auth.jwt.service;

import java.util.Set;
import org.bootstrapbugz.api.user.payload.dto.RoleDto;

public interface AccessTokenService {
  String create(Long userId, Set<RoleDto> roleDtos);

  void check(String token);

  void invalidate(String token);

  void invalidateAllByUser(Long userId);
}
