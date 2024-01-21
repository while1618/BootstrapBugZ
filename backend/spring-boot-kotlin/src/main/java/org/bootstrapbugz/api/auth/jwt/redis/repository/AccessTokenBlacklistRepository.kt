package org.bootstrapbugz.api.auth.jwt.redis.repository

import org.bootstrapbugz.api.auth.jwt.redis.model.AccessTokenBlacklist
import org.springframework.data.repository.CrudRepository

interface AccessTokenBlacklistRepository : CrudRepository<AccessTokenBlacklist, String>
