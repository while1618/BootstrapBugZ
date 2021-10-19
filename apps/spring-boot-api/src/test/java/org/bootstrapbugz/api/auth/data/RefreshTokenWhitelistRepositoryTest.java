package org.bootstrapbugz.api.auth.data;

import org.bootstrapbugz.api.auth.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@DirtiesContext
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenWhitelistRepositoryTest extends DatabaseContainers {
  private final RefreshTokenWhitelist first =
      new RefreshTokenWhitelist("token123", 22L, "ip1", 1000);
  private final RefreshTokenWhitelist second =
      new RefreshTokenWhitelist("token321", 22L, "ip2", 1000);
  private final RefreshTokenWhitelist third =
      new RefreshTokenWhitelist("token213", 23L, "ip3", 1000);

  @Autowired private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;

  @BeforeAll
  void setUp() {
    refreshTokenWhitelistRepository.saveAll(List.of(first, second, third));
  }

  @AfterAll
  void cleanUp() {
    refreshTokenWhitelistRepository.deleteAll(List.of(first, second, third));
  }

  @Test
  void itShouldFindByUserIdAndIpAddress() {
    var refreshToken =
        refreshTokenWhitelistRepository.findByUserIdAndIpAddress(23L, "ip3").orElseThrow();
    assertThat(refreshToken).isEqualTo(third);
  }

  @Test
  void itShouldFindAllByUserId() {
    var refreshTokens = refreshTokenWhitelistRepository.findAllByUserId(22L);
    assertThat(refreshTokens)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(List.of(first, second));
  }
}
