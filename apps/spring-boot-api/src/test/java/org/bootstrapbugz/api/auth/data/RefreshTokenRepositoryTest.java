package org.bootstrapbugz.api.auth.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ActiveProfiles;

@DataRedisTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class RefreshTokenRepositoryTest {
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  private final RefreshToken first = new RefreshToken("token123", "user", "127.0.0.1");
  private final RefreshToken second = new RefreshToken("token321", "user", "127.0.1.1");
  private final RefreshToken third = new RefreshToken("token213", "test", "127.0.0.1");

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
    RefreshToken expectedRefreshToken = new RefreshToken("token123", "user", "127.0.0.1");
    RefreshToken actualResponse =
        refreshTokenRepository.findByUsernameAndIpAddress("user", "127.0.0.1").orElseThrow();
    assertThat(actualResponse).isEqualTo(expectedRefreshToken);
  }

  @Test
  void itShouldFindAllByUsername() {
    RefreshToken first = new RefreshToken("token123", "user", "127.0.0.1");
    RefreshToken second = new RefreshToken("token321", "user", "127.0.1.1");
    List<RefreshToken> actualTokens = refreshTokenRepository.findAllByUsername("user");
    assertThat(actualTokens)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(List.of(first, second));
  }
}
