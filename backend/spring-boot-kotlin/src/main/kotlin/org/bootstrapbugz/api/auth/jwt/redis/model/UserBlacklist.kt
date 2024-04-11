package org.bootstrapbugz.api.auth.jwt.redis.model

import java.io.Serial
import java.io.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash(value = "UserBlacklist")
class UserBlacklist(
  @Id val userId: Long? = null,
  @TimeToLive val timeToLive: Long = 0,
  val updatedAt: Instant = Instant.now().truncatedTo(ChronoUnit.SECONDS)
) : Serializable {

  companion object {
    @Serial private val serialVersionUID = 8334740937644612692L
  }
}
