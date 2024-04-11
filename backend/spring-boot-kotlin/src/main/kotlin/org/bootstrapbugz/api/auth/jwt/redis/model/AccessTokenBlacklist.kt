package org.bootstrapbugz.api.auth.jwt.redis.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.io.Serial
import java.io.Serializable

@RedisHash(value = "AccessTokenBlacklist")
data class AccessTokenBlacklist(@Id val accessToken: String, @TimeToLive val timeToLive: Long) : Serializable {
    companion object {
        @Serial
        private const val serialVersionUID = 7371548317284111557L
    }
}
