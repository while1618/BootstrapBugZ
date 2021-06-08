package org.bootstrapbugz.api.auth.redis.repository;

import org.bootstrapbugz.api.auth.redis.model.JwtBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistRepository extends CrudRepository<JwtBlacklist, String> {}
