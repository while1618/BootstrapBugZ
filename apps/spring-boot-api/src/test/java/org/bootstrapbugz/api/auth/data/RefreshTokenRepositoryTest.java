package org.bootstrapbugz.api.auth.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
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

@DataRedisTest
@DirtiesContext
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryTest extends DatabaseContainers {
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  private final RefreshToken first = new RefreshToken("token123", "dataRedisTest1", "ip1", 1000);
  private final RefreshToken second = new RefreshToken("token321", "dataRedisTest1", "ip2", 1000);
  private final RefreshToken third = new RefreshToken("token213", "dataRedisTest2", "ip3", 1000);

  @BeforeAll
  void setUp() {
    refreshTokenRepository.saveAll(List.of(first, second, third));
  }

  @AfterAll
  void cleanUp() {
    refreshTokenRepository.deleteAll(List.of(first, second, third));
  }

  @Test
  void itShouldFindByUsernameAndIpAddress() {
    var refreshToken =
        refreshTokenRepository.findByUsernameAndIpAddress("dataRedisTest2", "ip3").orElseThrow();
    assertThat(refreshToken).isEqualTo(third);
  }

  @Test
  void itShouldFindAllByUsername() {
    var refreshTokens = refreshTokenRepository.findAllByUsername("dataRedisTest1");
    assertThat(refreshTokens)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(List.of(first, second));
  }
}
