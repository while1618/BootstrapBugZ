package org.bootstrapbugz.api.auth.jwt.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "UserBlacklist")
public class UserBlacklist implements Serializable {
  @Serial private static final long serialVersionUID = 8334740937644612692L;

  @Id private Long userId;

  private Instant updatedAt;

  @TimeToLive private long timeToLive;
}
