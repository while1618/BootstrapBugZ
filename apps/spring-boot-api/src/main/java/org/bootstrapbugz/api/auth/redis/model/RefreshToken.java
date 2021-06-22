package org.bootstrapbugz.api.auth.redis.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@RedisHash(value = "RefreshToken", timeToLive = 604800) // 7 days
public class RefreshToken implements Serializable {
  @Serial private static final long serialVersionUID = -1997218842142407911L;

  @Id private String token;

  @Indexed private String username;

  @Indexed private String ipAddress;
}
