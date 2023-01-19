package org.bootstrapbugz.api.auth.jwt.redis.repository;

import org.bootstrapbugz.api.auth.jwt.redis.model.AccessTokenBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenBlacklistRepository
    extends CrudRepository<AccessTokenBlacklist, String> {}
