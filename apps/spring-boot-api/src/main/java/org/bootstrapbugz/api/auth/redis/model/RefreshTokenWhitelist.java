package org.bootstrapbugz.api.auth.redis.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@RedisHash(value = "RefreshTokenWhitelist")
public class RefreshTokenWhitelist implements Serializable {
  @Serial private static final long serialVersionUID = -1997218842142407911L;

  @Id private String refreshToken;

  @Indexed private Long userId;

  @Indexed private String ipAddress;

  @TimeToLive private long timeToLive;
}
