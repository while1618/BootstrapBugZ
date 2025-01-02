package org.bugzkit.api.auth.jwt.redis.repository;

import org.bugzkit.api.auth.jwt.redis.model.UserBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface UserBlacklistRepository extends CrudRepository<UserBlacklist, Long> {}
