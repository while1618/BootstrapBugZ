package org.bootstrapbugz.api.auth.redis.repository;

import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}
