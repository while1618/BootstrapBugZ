package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.RefreshTokenStore;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenStoreRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.RefreshTokenServiceImpl;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
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
class RefreshTokenServiceTest {
  @Mock private RefreshTokenStoreRepository refreshTokenStoreRepository;
  @Mock private MessageService messageService;
  @InjectMocks private RefreshTokenServiceImpl refreshTokenService;
  @Captor private ArgumentCaptor<RefreshTokenStore> refreshTokenArgumentCaptor;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(refreshTokenService, "secret", "secret");
    ReflectionTestUtils.setField(refreshTokenService, "tokenDuration", 604800);
  }

  @Test
  void createToken() {
    final var refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    final var expectedRefreshToken = new RefreshTokenStore(refreshToken, 1L, "ip1", 1000);
    verify(refreshTokenStoreRepository, times(1)).save(refreshTokenArgumentCaptor.capture());
    assertThat(refreshTokenArgumentCaptor.getValue().getRefreshToken())
        .isEqualTo(expectedRefreshToken.getRefreshToken());
    assertThat(refreshTokenArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedRefreshToken.getUserId());
    assertThat(refreshTokenArgumentCaptor.getValue().getIpAddress())
        .isEqualTo(expectedRefreshToken.getIpAddress());
  }

  @Test
  void checkRefreshToken() {
    final var refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    when(refreshTokenStoreRepository.existsById(refreshToken)).thenReturn(true);
    refreshTokenService.check(refreshToken);
  }

  @Test
  void checkRefreshToken_throwBadRequest_refreshTokenNotInRedis() {
    final var refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    when(refreshTokenStoreRepository.existsById(refreshToken)).thenReturn(false);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> refreshTokenService.check(refreshToken))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void findRefreshTokenByUserIdAndIpAddress() {
    final var refreshToken = new RefreshTokenStore("token123", 1L, "ip1", 1000);
    when(refreshTokenStoreRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    final var token = refreshTokenService.findByUserIdAndIpAddress(1L, "ip1");
    assertThat(token).isPresent();
  }

  @Test
  void deleteRefreshToken() {
    final var refreshToken = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    refreshTokenService.delete(refreshToken);
    verify(refreshTokenStoreRepository, times(1)).deleteById(any(String.class));
  }

  @Test
  void deleteRefreshTokenByUserAndIpAddress() {
    final var refreshToken = new RefreshTokenStore("token123", 1L, "ip1", 1000);
    when(refreshTokenStoreRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    refreshTokenService.deleteByUserIdAndIpAddress(1L, "ip1");
    verify(refreshTokenStoreRepository, times(1)).delete(any(RefreshTokenStore.class));
  }

  @Test
  void deleteAllRefreshTokensByUser() {
    final var refreshToken1 = new RefreshTokenStore("token123", 1L, "ip1", 1000);
    final var refreshToken2 = new RefreshTokenStore("token321", 1L, "ip2", 1000);
    when(refreshTokenStoreRepository.findAllByUserId(1L))
        .thenReturn(List.of(refreshToken1, refreshToken2));
    refreshTokenService.deleteAllByUserId(1L);
    verify(refreshTokenStoreRepository, times(1)).deleteAll(List.of(refreshToken1, refreshToken2));
  }
}
