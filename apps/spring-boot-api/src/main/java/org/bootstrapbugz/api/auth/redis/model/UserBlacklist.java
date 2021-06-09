package org.bootstrapbugz.api.auth.redis.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "UserBlacklist", timeToLive = 900) // 15min
public class UserBlacklist implements Serializable {
  @Serial private static final long serialVersionUID = 8334740937644612692L;

  @Id private String username;

  private Date updatedAt;
}
