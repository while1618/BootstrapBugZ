package org.bootstrapbugz.api.auth.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenStoreRepository;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataRedisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenStoreRepositoryIT extends DatabaseContainers {
  @Autowired private RefreshTokenStoreRepository refreshTokenStoreRepository;

  @BeforeEach
  void setUp() {
    final var first = new RefreshTokenStore("token1", 1L, "ip1", 1000);
    final var second = new RefreshTokenStore("token2", 2L, "ip2", 1000);
    final var third = new RefreshTokenStore("token3", 2L, "ip3", 1000);
    refreshTokenStoreRepository.saveAll(List.of(first, second, third));
  }

  @AfterEach
  void cleanUp() {
    refreshTokenStoreRepository.deleteAll();
  }

  @Test
  void itShouldFindByUserIdAndIpAddress() {
    assertThat(refreshTokenStoreRepository.findByUserIdAndIpAddress(1L, "ip1")).isPresent();
  }

  @Test
  void itShouldFindAllByUserId() {
    assertThat(refreshTokenStoreRepository.findAllByUserId(2L)).hasSize(2);
  }
}
