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
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
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
  void createToken() {
    final var token = resetPasswordTokenService.create(1L);
    assertThat(token).isNotNull();
  }

  @Test
  void checkToken_userNotInBlacklist() {
    final var token = resetPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    resetPasswordTokenService.check(token);
  }

  @Test
  void checkToken_userInBlacklist_tokenIssuedAfterUserBlacklisted() {
    final var userBlacklist =
        new UserBlacklist(1L, 1000, Instant.now().truncatedTo(ChronoUnit.SECONDS).minusSeconds(10));
    final var token = resetPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    resetPasswordTokenService.check(token);
  }

  @Test
  void checkToken_throwBadRequest_userInBlacklist() {
    final var token = resetPasswordTokenService.create(1L);
    final var userBlacklist =
        new UserBlacklist(1L, 1000, Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10));
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> resetPasswordTokenService.check(token))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid token.");
  }
}
