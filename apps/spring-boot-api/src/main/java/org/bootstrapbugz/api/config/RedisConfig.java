package org.bootstrapbugz.api.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@Profile({"dev", "prod"})
@EnableRedisRepositories(enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {
  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Value("${spring.redis.database}")
  private int database;

  @Value("${spring.redis.password}")
  private String password;

  @Value("${spring.redis.timeout}")
  private int timeout;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    redisStandaloneConfiguration.setDatabase(database);
    redisStandaloneConfiguration.setPassword(RedisPassword.of(password));

    var jedisClientConfiguration =
        JedisClientConfiguration.builder().connectTimeout(Duration.ofSeconds(timeout));

    return new JedisConnectionFactory(
        redisStandaloneConfiguration, jedisClientConfiguration.build());
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    var template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
  }
}
