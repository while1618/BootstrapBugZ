package org.bootstrapbugz.api.auth.jwt.redis.repository

import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist
import org.springframework.data.repository.CrudRepository

interface UserBlacklistRepository : CrudRepository<UserBlacklist, Long>
