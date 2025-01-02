package org.bugzkit.api.auth.jwt.redis.repository;

import org.bugzkit.api.auth.jwt.redis.model.AccessTokenBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenBlacklistRepository
    extends CrudRepository<AccessTokenBlacklist, String> {}
