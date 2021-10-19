package org.bootstrapbugz.api.auth.unit;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.bootstrapbugz.api.auth.redis.model.AccessTokenBlacklist;
import org.bootstrapbugz.api.auth.redis.model.RefreshTokenWhitelist;
import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.auth.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
class JwtServiceTest {
  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  @Mock private MessageService messageService;

  @InjectMocks private JwtServiceImpl jwtService;

  @Captor private ArgumentCaptor<AccessTokenBlacklist> accessTokenBlacklistArgumentCaptor;
  @Captor private ArgumentCaptor<UserBlacklist> userBlacklistArgumentCaptor;
  @Captor private ArgumentCaptor<RefreshTokenWhitelist> refreshTokenArgumentCaptor;

  @Test
  void itShouldCreateToken() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);
  }

  @Test
  void checkTokenShouldThrowTokenExpired() {
    String token =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzdWVkQXQiOiIyMDIxLTA2LTI0VDIzOjI3OjE4LjQ0Mjk1OTUxMloiLCJleHAiOjE2MjQ1NzcyMzh9.OJnpb4OR_j6XN7Oj0P3wac2t1tNW_ZL6Ka6G6JzOcAIY9WDFUlk4TjXoLfmXtixvfRIGKd6WplHXzDfvs75M1w";
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(TokenExpiredException.class);
  }

  @Test
  void checkTokenShouldThrowUnauthorized_tokenInvalidated() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(true);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void checkTokenShouldThrowUnauthorized_userInBlacklist() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    when(accessTokenBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldInvalidateToken() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);

    var expectedAccessTokenBlacklist = new AccessTokenBlacklist(token, 1000);
    jwtService.invalidateToken(token);
    verify(accessTokenBlacklistRepository, times(1))
        .save(accessTokenBlacklistArgumentCaptor.capture());
    assertThat(accessTokenBlacklistArgumentCaptor.getValue().getAccessToken())
        .isEqualTo(expectedAccessTokenBlacklist.getAccessToken());
  }

  @Test
  void itShouldInvalidateAllTokens() {
    var expectedUserBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    jwtService.invalidateAllTokens(1L);
    verify(userBlacklistRepository, times(1)).save(userBlacklistArgumentCaptor.capture());
    assertThat(userBlacklistArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedUserBlacklist.getUserId());
  }

  @Test
  void itShouldCreateRefreshToken() {
    String actualRefreshToken = jwtService.createRefreshToken(1L, Collections.emptySet(), "ip1");
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
    String refreshToken = jwtService.createRefreshToken(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(refreshToken)).thenReturn(true);
    jwtService.checkRefreshToken(refreshToken);
  }

  @Test
  void checkRefreshTokenShouldThrowTokenExpired() {
    String refreshToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzdWVkQXQiOiIyMDIxLTA2LTI0VDIzOjQ3OjQ5LjI1NzMwNDEzOVoiLCJleHAiOjE2MjQ1Nzg0Njl9.PwpJDAkepN--qyMDuD4eJZRLc_NSIYgfc5kLX6as2sRaktuC4n-Hr065E4I1WSkco5kgxt4vNsPwezuniHXkqg";
    assertThatThrownBy(() -> jwtService.checkRefreshToken(refreshToken))
        .isInstanceOf(TokenExpiredException.class);
  }

  @Test
  void checkRefreshTokenShouldThrowUnauthorized_refreshTokenNotInRedis() {
    String refreshToken = jwtService.createRefreshToken(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(refreshToken)).thenReturn(false);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkRefreshToken(refreshToken))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldFindRefreshToken() {
    var refreshToken = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    when(refreshTokenWhitelistRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    String token = jwtService.findRefreshToken(1L, "ip1");
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldDeleteRefreshToken() {
    String refreshToken = jwtService.createRefreshToken(1L, Collections.emptySet(), "ip1");
    jwtService.deleteRefreshToken(refreshToken);
    verify(refreshTokenWhitelistRepository, times(1)).deleteById(any(String.class));
  }

  @Test
  void itShouldDeleteRefreshTokenByUserAndIpAddress() {
    var refreshToken = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    when(refreshTokenWhitelistRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    jwtService.deleteRefreshTokenByUserAndIpAddress(1L, "ip1");
    verify(refreshTokenWhitelistRepository, times(1)).delete(any(RefreshTokenWhitelist.class));
  }

  @Test
  void itShouldDeleteAllRefreshTokensByUser() {
    var refreshToken1 = new RefreshTokenWhitelist("token123", 1L, "ip1", 1000);
    var refreshToken2 = new RefreshTokenWhitelist("token321", 1L, "ip2", 1000);
    when(refreshTokenWhitelistRepository.findAllByUserId(1L))
        .thenReturn(List.of(refreshToken1, refreshToken2));
    jwtService.deleteAllRefreshTokensByUser(1L);
    verify(refreshTokenWhitelistRepository, times(1))
        .deleteAll(List.of(refreshToken1, refreshToken2));
  }
}
