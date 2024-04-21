package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.AccessTokenBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.AccessTokenServiceImpl;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceTest {
  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private MessageService messageService;
  @InjectMocks private AccessTokenServiceImpl accessTokenService;
  @Captor private ArgumentCaptor<AccessTokenBlacklist> accessTokenBlacklistArgumentCaptor;
  @Captor private ArgumentCaptor<UserBlacklist> userBlacklistArgumentCaptor;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(accessTokenService, "secret", "secret");
    ReflectionTestUtils.setField(accessTokenService, "tokenDuration", 900);
  }

  @Test
  void createToken() {
    final var token = accessTokenService.create(1L, Collections.emptySet());
    assertThat(token).isNotNull();
  }

  @Test
  void checkToken_userNotInBlacklist() {
    final var token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    accessTokenService.check(token);
  }

  @Test
  void checkToken_userInBlacklist_tokenIssuedAfterUserBlacklisted() {
    final var userBlacklist =
        UserBlacklist.builder()
            .userId(1L)
            .updatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).minusSeconds(10))
            .timeToLive(1000)
            .build();
    final var token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    accessTokenService.check(token);
  }

  @Test
  void checkToken_throwUnauthorized_tokenInvalidated() {
    final var token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(true);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> accessTokenService.check(token))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void checkToken_throwUnauthorized_userInBlacklist() {
    final var token = accessTokenService.create(1L, Collections.emptySet());
    final var userBlacklist =
        UserBlacklist.builder()
            .userId(1L)
            .updatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10))
            .timeToLive(1000)
            .build();
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> accessTokenService.check(token))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void invalidateToken() {
    final var token = accessTokenService.create(1L, Collections.emptySet());
    final var expectedAccessTokenBlacklist = new AccessTokenBlacklist(token, 1000);
    accessTokenService.invalidate(token);
    verify(accessTokenBlacklistRepository, times(1))
        .save(accessTokenBlacklistArgumentCaptor.capture());
    assertThat(accessTokenBlacklistArgumentCaptor.getValue().getAccessToken())
        .isEqualTo(expectedAccessTokenBlacklist.getAccessToken());
  }

  @Test
  void invalidateAllTokens() {
    final var expectedUserBlacklist = UserBlacklist.builder().userId(1L).timeToLive(1000).build();
    accessTokenService.invalidateAllByUserId(1L);
    verify(userBlacklistRepository, times(1)).save(userBlacklistArgumentCaptor.capture());
    assertThat(userBlacklistArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedUserBlacklist.getUserId());
  }
}
