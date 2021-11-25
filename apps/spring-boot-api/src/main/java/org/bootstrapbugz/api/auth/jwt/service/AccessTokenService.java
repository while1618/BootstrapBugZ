package org.bootstrapbugz.api.auth.jwt.service;

import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;

public interface AccessTokenService {
  String create(Long userId, Set<Role> roles);

  void check(String token);

  void invalidate(String token);

  void invalidateAllByUser(Long userId);
}
