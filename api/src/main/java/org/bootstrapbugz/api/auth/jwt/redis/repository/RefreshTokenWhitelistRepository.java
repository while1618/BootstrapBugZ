package org.bootstrapbugz.api.auth.jwt.redis.repository;

import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenWhitelist;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenWhitelistRepository
    extends CrudRepository<RefreshTokenWhitelist, String> {
  Optional<RefreshTokenWhitelist> findByUserIdAndIpAddress(Long userId, String ipAddress);

  List<RefreshTokenWhitelist> findAllByUserId(Long userId);
}
