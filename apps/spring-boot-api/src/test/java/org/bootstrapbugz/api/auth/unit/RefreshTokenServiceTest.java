package org.bootstrapbugz.api.auth.unit;

import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.RefreshTokenServiceImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
  @Mock private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  @Mock private MessageService messageService;

  @InjectMocks private RefreshTokenServiceImpl refreshTokenService;

  @Captor private ArgumentCaptor<RefreshTokenWhitelist> refreshTokenArgumentCaptor;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(refreshTokenService, "secret", "secret");
  }

  @Test
  void itShouldCreateToken() {
    String actualRefreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    var expectedRefreshToken = new RefreshTokenWhitelist(actualRefreshToken, 1L, "ip1", 1000);
    verify(refreshTokenWhitelistRepository, times(1)).save(refreshTokenArgumentCaptor.capture());
    assertThat(refreshTokenArgumentCaptor.getValue().getRefreshToken())
        .isEqualTo(expectedRefreshToken.getRefreshToken());
    assertThat(refreshTokenArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedRefreshToken.getUserId());
    assertThat(refreshTokenArgumentCaptor.getValue().getIpAddress())
        .isEqualTo(expectedRefreshToken.getIpAddress());
  }

  @Test
  void itShouldCheckRefreshToken() {
    String refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(refreshToken)).thenReturn(true);
    refreshTokenService.check(refreshToken);
  }

  @Test
  void checkRefreshTokenShouldThrowUnauthorized_refreshTokenNotInRedis() {
    String refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(refreshToken)).thenReturn(false);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> refreshTokenService.check(refreshToken))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldFindRefreshToken() {
    var refreshToken = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    when(refreshTokenWhitelistRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    String token = refreshTokenService.findByUserAndIpAddress(1L, "ip1");
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldDeleteRefreshToken() {
    String refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    refreshTokenService.delete(refreshToken);
    verify(refreshTokenWhitelistRepository, times(1)).deleteById(any(String.class));
  }

  @Test
  void itShouldDeleteRefreshTokenByUserAndIpAddress() {
    var refreshToken = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    when(refreshTokenWhitelistRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    refreshTokenService.deleteByUserAndIpAddress(1L, "ip1");
    verify(refreshTokenWhitelistRepository, times(1)).delete(any(RefreshTokenWhitelist.class));
  }

  @Test
  void itShouldDeleteAllRefreshTokensByUser() {
    var refreshToken1 = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    var refreshToken2 = new RefreshTokenWhitelist("token321", 1L, "ip2", 1000);
    when(refreshTokenWhitelistRepository.findAllByUserId(1L))
        .thenReturn(List.of(refreshToken1, refreshToken2));
    refreshTokenService.deleteAllByUser(1L);
    verify(refreshTokenWhitelistRepository, times(1))
        .deleteAll(List.of(refreshToken1, refreshToken2));
  }
}
