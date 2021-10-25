package org.bootstrapbugz.api.auth.jwt.redis.repository;

import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenWhitelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenWhitelistRepository
    extends CrudRepository<RefreshTokenWhitelist, String> {
  Optional<RefreshTokenWhitelist> findByUserIdAndIpAddress(Long userId, String ipAddress);

  List<RefreshTokenWhitelist> findAllByUserId(Long userId);
}
