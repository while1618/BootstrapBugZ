package org.bootstrapbugz.api.auth.jwt.redis.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import java.io.Serial
import java.io.Serializable

@RedisHash(value = "RefreshTokenStore")
class RefreshTokenStore(
    @Id val refreshToken: String? = null,
    @Indexed val userId: Long? = null,
    @Indexed val ipAddress: String? = null,
    @TimeToLive val timeToLive: Long = 0
) : Serializable {

    companion object {
        @Serial
        private const val serialVersionUID = -1997218842142407911L
    }
}
