package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.ResetPasswordTokenServiceImpl;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTokenServiceTest {
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private MessageService messageService;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ResetPasswordTokenServiceImpl resetPasswordTokenService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(resetPasswordTokenService, "secret", "secret");
    ReflectionTestUtils.setField(resetPasswordTokenService, "tokenDuration", 900);
  }

  @Test
  void itShouldCreateToken() {
    final var token = resetPasswordTokenService.create(1L);
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    final var token = resetPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    resetPasswordTokenService.check(token);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    final var userBlacklist =
        new UserBlacklist()
            .setUserId(1L)
            .setUpdatedAt(Instant.now().minusSeconds(1).truncatedTo(ChronoUnit.SECONDS))
            .setTimeToLive(1000);
    final var token = resetPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    resetPasswordTokenService.check(token);
  }

  @Test
  void checkTokenShouldThrowUnauthorized_userInBlacklist() {
    final var token = resetPasswordTokenService.create(1L);
    final var userBlacklist =
        new UserBlacklist()
            .setUserId(1L)
            .setTimeToLive(1000)
            .setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.MILLIS).plusMillis(1));
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> resetPasswordTokenService.check(token))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }
}
