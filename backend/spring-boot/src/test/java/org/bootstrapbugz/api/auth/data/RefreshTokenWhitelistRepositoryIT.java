package org.bootstrapbugz.api.auth.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataRedisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenWhitelistRepositoryIT extends DatabaseContainers {
  @Autowired private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;

  @BeforeEach
  void setUp() {
    final var first = new RefreshTokenWhitelist("token123", 22L, "ip1", 1000);
    final var second = new RefreshTokenWhitelist("token321", 22L, "ip2", 1000);
    final var third = new RefreshTokenWhitelist("token213", 23L, "ip3", 1000);
    refreshTokenWhitelistRepository.saveAll(List.of(first, second, third));
  }

  @AfterEach
  void cleanUp() {
    refreshTokenWhitelistRepository.deleteAll();
  }

  @Test
  void itShouldFindByUserIdAndIpAddress() {
    assertThat(refreshTokenWhitelistRepository.findByUserIdAndIpAddress(23L, "ip3")).isPresent();
  }

  @Test
  void itShouldFindAllByUserId() {
    assertThat(refreshTokenWhitelistRepository.findAllByUserId(22L)).hasSize(2);
  }
}
