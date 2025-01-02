package org.bugzkit.api.auth.jwt.redis.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "AccessTokenBlacklist")
public class AccessTokenBlacklist implements Serializable {
  @Serial private static final long serialVersionUID = 7371548317284111557L;

  @Id private String accessToken;

  @TimeToLive private long timeToLive;
}
