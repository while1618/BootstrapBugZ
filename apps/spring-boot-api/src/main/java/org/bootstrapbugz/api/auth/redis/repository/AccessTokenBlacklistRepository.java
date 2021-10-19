package org.bootstrapbugz.api.auth.redis.repository;

import org.bootstrapbugz.api.auth.redis.model.AccessTokenBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenBlacklistRepository
    extends CrudRepository<AccessTokenBlacklist, String> {}
