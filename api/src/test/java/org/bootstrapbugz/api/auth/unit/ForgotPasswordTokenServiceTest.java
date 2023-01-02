package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.bootstrapbugz.api.auth.jwt.redis.model.UserBlacklist;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.ForgotPasswordTokenServiceImpl;
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
class ForgotPasswordTokenServiceTest {
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private MessageService messageService;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ForgotPasswordTokenServiceImpl forgotPasswordTokenService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(forgotPasswordTokenService, "secret", "secret");
  }

  @Test
  void itShouldCreateToken() {
    final var token = forgotPasswordTokenService.create(1L);
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckToken_userNotInBlacklist() {
    final var token = forgotPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.empty());
    forgotPasswordTokenService.check(token);
  }

  @Test
  void itShouldCheckToken_userInBlacklistButTokenIsIssuedAfter() {
    final var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    final var token = forgotPasswordTokenService.create(1L);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    forgotPasswordTokenService.check(token);
  }

  @Test
  void checkTokenShouldThrowUnauthorized_userInBlacklist() {
    final var token = forgotPasswordTokenService.create(1L);
    final var userBlacklist = new UserBlacklist(1L, Instant.now(), 1000);
    when(userBlacklistRepository.findById(1L)).thenReturn(Optional.of(userBlacklist));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> forgotPasswordTokenService.check(token))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }
}
