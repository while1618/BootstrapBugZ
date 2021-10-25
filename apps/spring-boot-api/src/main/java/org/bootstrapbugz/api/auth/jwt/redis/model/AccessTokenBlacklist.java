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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "AccessTokenBlacklist")
public class AccessTokenBlacklist implements Serializable {
  @Serial private static final long serialVersionUID = 7371548317284111557L;

  @Id private String accessToken;

  @TimeToLive private long timeToLive;
}
