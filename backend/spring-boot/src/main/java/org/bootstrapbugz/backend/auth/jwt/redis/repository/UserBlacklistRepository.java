package org.bootstrapbugz.backend.auth.jwt.redis.repository;

import org.bootstrapbugz.backend.auth.jwt.redis.model.UserBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface UserBlacklistRepository extends CrudRepository<UserBlacklist, Long> {}
