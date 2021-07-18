package org.bootstrapbugz.api.auth.redis.repository;

import java.util.List;
import java.util.Optional;

import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
  Optional<RefreshToken> findByUserIdAndIpAddress(Long userId, String ipAddress);

  List<RefreshToken> findAllByUserId(Long userId);
}
