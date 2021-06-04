package org.bootstrapbugz.api.auth.repository;

import org.bootstrapbugz.api.auth.model.UserBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlacklistRepository extends CrudRepository<UserBlacklist, String> {}
