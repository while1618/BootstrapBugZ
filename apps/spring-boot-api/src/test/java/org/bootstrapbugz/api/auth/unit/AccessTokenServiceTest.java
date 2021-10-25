package org.bootstrapbugz.api.auth.unit;

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

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  }

  @Test
  void itShouldCreateToken() {
    String token = accessTokenService.create(1L, Collections.emptySet());
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    String token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    accessTokenService.check(token);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    String token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    accessTokenService.check(token);
  }

  @Test
  void checkTokenShouldThrowUnauthorized_tokenInvalidated() {
    String token = accessTokenService.create(1L, Collections.emptySet());
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(true);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> accessTokenService.check(token))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void checkTokenShouldThrowUnauthorized_userInBlacklist() {
    String token = accessTokenService.create(1L, Collections.emptySet());
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> accessTokenService.check(token))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldInvalidateToken() {
    String token = accessTokenService.create(1L, Collections.emptySet());
    var expectedAccessTokenBlacklist = new AccessTokenBlacklist(token, 1000);
    accessTokenService.invalidate(token);
    verify(accessTokenBlacklistRepository, times(1))
        .save(accessTokenBlacklistArgumentCaptor.capture());
    assertThat(accessTokenBlacklistArgumentCaptor.getValue().getAccessToken())
        .isEqualTo(expectedAccessTokenBlacklist.getAccessToken());
  }

  @Test
  void itShouldInvalidateAllTokens() {
    var expectedUserBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    accessTokenService.invalidateAllByUser(1L);
    verify(userBlacklistRepository, times(1)).save(userBlacklistArgumentCaptor.capture());
    assertThat(userBlacklistArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedUserBlacklist.getUserId());
  }
}
