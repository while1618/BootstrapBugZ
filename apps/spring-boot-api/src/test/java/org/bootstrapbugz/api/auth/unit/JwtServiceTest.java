package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.auth0.jwt.exceptions.TokenExpiredException;

import org.bootstrapbugz.api.auth.redis.model.JwtBlacklist;
import org.bootstrapbugz.api.auth.redis.model.RefreshToken;
import org.bootstrapbugz.api.auth.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.redis.repository.JwtBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
import org.bootstrapbugz.api.auth.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
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
    String token = jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES);
    assertThat(token).isNotNull().startsWith(JwtUtil.TOKEN_TYPE);
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES));
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById("user")).thenReturn(Optional.empty());
    jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    UserBlacklist userBlacklist = new UserBlacklist("user", Instant.now(), 1000);
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES));
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById("user")).thenReturn(Optional.of(userBlacklist));
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
  void checkTokenShouldThrowForbidden_tokenInvalidated() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES));
    when(jwtBlacklistRepository.existsById(token)).thenReturn(true);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void checkTokenShouldThrowForbidden_userInBlacklist() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES));
    UserBlacklist userBlacklist = new UserBlacklist("user", Instant.now(), 1000);
    when(jwtBlacklistRepository.existsById(token)).thenReturn(false);
    when(userBlacklistRepository.findById("user")).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkToken(token, JwtPurpose.ACCESSING_RESOURCES))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldInvalidateToken() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("user", JwtPurpose.ACCESSING_RESOURCES));
    JwtBlacklist expectedJwtBlacklist = new JwtBlacklist(token, 1000);
    jwtService.invalidateToken(token);
    verify(jwtBlacklistRepository, times(1)).save(jwtBlacklistArgumentCaptor.capture());
    assertThat(jwtBlacklistArgumentCaptor.getValue().getToken())
        .isEqualTo(expectedJwtBlacklist.getToken());
  }

  @Test
  void itShouldInvalidateAllTokens() {
    UserBlacklist expectedUserBlacklist = new UserBlacklist("user", Instant.now(), 1000);
    jwtService.invalidateAllTokens("user");
    verify(userBlacklistRepository, times(1)).save(userBlacklistArgumentCaptor.capture());
    assertThat(userBlacklistArgumentCaptor.getValue().getUsername())
        .isEqualTo(expectedUserBlacklist.getUsername());
  }

  @Test
  void itShouldCreateRefreshToken() {
    String actualRefreshToken =
        JwtUtil.removeTokenTypeFromToken(jwtService.createRefreshToken("user", "ip1"));
    RefreshToken expectedRefreshToken = new RefreshToken(actualRefreshToken, "user", "ip1", 1000);
    verify(refreshTokenRepository, times(1)).save(refreshTokenArgumentCaptor.capture());
    assertThat(refreshTokenArgumentCaptor.getValue().getToken())
        .isEqualTo(expectedRefreshToken.getToken());
    assertThat(refreshTokenArgumentCaptor.getValue().getUsername())
        .isEqualTo(expectedRefreshToken.getUsername());
    assertThat(refreshTokenArgumentCaptor.getValue().getIpAddress())
        .isEqualTo(expectedRefreshToken.getIpAddress());
  }

  @Test
  void itShouldCheckRefreshToken() {
    String refreshToken =
        JwtUtil.removeTokenTypeFromToken(jwtService.createRefreshToken("user", "ip1"));
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
  void checkRefreshTokenShouldThrowForbidden_refreshTokenNotInRedis() {
    String refreshToken =
        JwtUtil.removeTokenTypeFromToken(jwtService.createRefreshToken("user", "ip1"));
    when(refreshTokenRepository.existsById(refreshToken)).thenReturn(false);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> jwtService.checkRefreshToken(refreshToken))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldFindRefreshToken() {
    RefreshToken refreshToken = new RefreshToken("token123", "user", "ip1", 1000);
    when(refreshTokenRepository.findByUsernameAndIpAddress("user", "ip1"))
        .thenReturn(Optional.of(refreshToken));
    String token = jwtService.findRefreshToken("user", "ip1");
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldDeleteRefreshToken() {
    String refreshToken =
        JwtUtil.removeTokenTypeFromToken(jwtService.createRefreshToken("user", "ip1"));
    jwtService.deleteRefreshToken(refreshToken);
    verify(refreshTokenRepository, times(1)).deleteById(any(String.class));
  }

  @Test
  void itShouldDeleteRefreshTokenByUserAndIpAddress() {
    RefreshToken refreshToken = new RefreshToken("token123", "user", "ip1", 1000);
    when(refreshTokenRepository.findByUsernameAndIpAddress("user", "ip1"))
        .thenReturn(Optional.of(refreshToken));
    jwtService.deleteRefreshTokenByUserAndIpAddress("user", "ip1");
    verify(refreshTokenRepository, times(1)).delete(any(RefreshToken.class));
  }

  @Test
  void itShouldDeleteAllRefreshTokensByUser() {
    RefreshToken refreshToken1 = new RefreshToken("token123", "user", "ip1", 1000);
    RefreshToken refreshToken2 = new RefreshToken("token321", "user", "ip2", 1000);
    when(refreshTokenRepository.findAllByUsername("user"))
        .thenReturn(List.of(refreshToken1, refreshToken2));
    jwtService.deleteAllRefreshTokensByUser("user");
    verify(refreshTokenRepository, times(1)).deleteAll(List.of(refreshToken1, refreshToken2));
  }
}
