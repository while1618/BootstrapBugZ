package org.bootstrapbugz.api.auth.jwt.redis.repository;

import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenStoreRepository extends CrudRepository<RefreshTokenStore, String> {
  Optional<RefreshTokenStore> findByUserIdAndIpAddress(Long userId, String ipAddress);

  List<RefreshTokenStore> findAllByUserId(Long userId);
}
