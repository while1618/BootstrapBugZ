package org.bootstrapbugz.api.auth.jwt.redis.repository

import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface RefreshTokenStoreRepository : CrudRepository<RefreshTokenStore, String> {
    fun findByUserIdAndIpAddress(userId: Long?, ipAddress: String): Optional<RefreshTokenStore>
    fun findAllByUserId(userId: Long?): List<RefreshTokenStore>
}
