package org.bootstrapbugz.api.shared.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class DatabaseContainers {
  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:latest");
  private static final GenericContainer<?> redisContainer =
      new GenericContainer<>("redis:latest")
          .withCommand("redis-server --requirepass root")
          .withExposedPorts(6379);

  static {
    postgreSQLContainer.start();
    redisContainer.start();
  }

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.redis.host", redisContainer::getHost);
    registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
  }
}
