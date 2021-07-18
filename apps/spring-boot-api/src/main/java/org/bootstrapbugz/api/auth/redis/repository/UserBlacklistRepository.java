package org.bootstrapbugz.api.auth.redis.repository;

import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlacklistRepository extends CrudRepository<UserBlacklist, Long> {}
