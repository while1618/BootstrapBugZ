package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.TokenExpiredException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.api.auth.redis.model.JwtBlacklist;
import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.redis.repository.JwtBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
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

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
  @Mock private JwtBlacklistRepository jwtBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private MessageService messageService;

  @InjectMocks private JwtServiceImpl jwtService;

  @Captor private ArgumentCaptor<JwtBlacklist> jwtBlacklistArgumentCaptor;
  @Captor private ArgumentCaptor<UserBlacklist> userBlacklistArgumentCaptor;
  @Captor private ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor;

  @Test
  void itShouldCreateToken() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
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
    when(jwtBlacklistRepository.existsById(token)).thenReturn(true);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void checkTokenShouldThrowUnauthorized_userInBlacklist() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldInvalidateToken() {
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);

    var expectedJwtBlacklist = new JwtBlacklist(token, 1000);
    jwtService.invalidateToken(token);
    verify(jwtBlacklistRepository, times(1)).save(jwtBlacklistArgumentCaptor.capture());
    assertThat(jwtBlacklistArgumentCaptor.getValue().getToken())
        .isEqualTo(expectedJwtBlacklist.getToken());
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
    String actualRefreshToken = jwtService.createRefreshToken(1L, "ip1");
    var expectedRefreshToken = new RefreshToken(actualRefreshToken, 1L, "ip1", 1000);
    verify(refreshTokenRepository, times(1)).save(refreshTokenArgumentCaptor.capture());
    assertThat(refreshTokenArgumentCaptor.getValue().getToken())
        .isEqualTo(expectedRefreshToken.getToken());
    assertThat(refreshTokenArgumentCaptor.getValue().getUserId())
        .isEqualTo(expectedRefreshToken.getUserId());
    assertThat(refreshTokenArgumentCaptor.getValue().getIpAddress())
        .isEqualTo(expectedRefreshToken.getIpAddress());
  }

  @Test
  void itShouldCheckRefreshToken() {
    String refreshToken = jwtService.createRefreshToken(1L, "ip1");
    when(refreshTokenRepository.existsById(refreshToken)).thenReturn(true);
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
    String refreshToken = jwtService.createRefreshToken(1L, "ip1");
    when(refreshTokenRepository.existsById(refreshToken)).thenReturn(false);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkRefreshToken(refreshToken))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldFindRefreshToken() {
    var refreshToken = new RefreshToken("token123", 1L, "ip1", 1000);
    when(refreshTokenRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    String token = jwtService.findRefreshToken(1L, "ip1");
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldDeleteRefreshToken() {
    String refreshToken = jwtService.createRefreshToken(1L, "ip1");
    jwtService.deleteRefreshToken(refreshToken);
    verify(refreshTokenRepository, times(1)).deleteById(any(String.class));
  }

  @Test
  void itShouldDeleteRefreshTokenByUserAndIpAddress() {
    var refreshToken = new RefreshToken("token123", 1L, "ip1", 1000);
    when(refreshTokenRepository.findByUserIdAndIpAddress(1L, "ip1"))
        .thenReturn(Optional.of(refreshToken));
    jwtService.deleteRefreshTokenByUserAndIpAddress(1L, "ip1");
    verify(refreshTokenRepository, times(1)).delete(any(RefreshToken.class));
  }

  @Test
  void itShouldDeleteAllRefreshTokensByUser() {
    var refreshToken1 = new RefreshToken("token123", 1L, "ip1", 1000);
    var refreshToken2 = new RefreshToken("token321", 1L, "ip2", 1000);
    when(refreshTokenRepository.findAllByUserId(1L))
        .thenReturn(List.of(refreshToken1, refreshToken2));
    jwtService.deleteAllRefreshTokensByUser(1L);
    verify(refreshTokenRepository, times(1)).deleteAll(List.of(refreshToken1, refreshToken2));
  }
}
