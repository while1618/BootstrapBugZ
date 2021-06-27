package org.bootstrapbugz.api.shared.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

import redis.embedded.RedisServer;

@TestConfiguration
@Profile({"test"})
public class RedisTestConfig {
  @Value("${spring.redis.port}")
  private int port;

  private RedisServer redisServer;

  @PostConstruct
  public void startRedis() {
    redisServer = RedisServer.builder().port(port).setting("maxmemory 128M").build();
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    redisServer.stop();
  }
}
