package org.bootstrapbugz.api.auth.redis.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RefreshToken", timeToLive = 604800) // 7 days
public class RefreshToken implements Serializable {
  @Serial private static final long serialVersionUID = -1997218842142407911L;

  @Id private String token;
}
