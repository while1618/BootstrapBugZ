package org.bootstrapbugz.api.auth.jwt.redis.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@RedisHash(value = "UserBlacklist")
public class UserBlacklist implements Serializable {
  @Serial private static final long serialVersionUID = 8334740937644612692L;

  @Id private Long userId;

  private Instant updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);

  @TimeToLive private long timeToLive;
}
