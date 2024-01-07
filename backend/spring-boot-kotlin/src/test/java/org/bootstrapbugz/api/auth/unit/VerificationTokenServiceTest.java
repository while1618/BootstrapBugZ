package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.bootstrapbugz.api.auth.jwt.service.impl.VerificationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {
  @Mock private ApplicationEventPublisher eventPublisher;
  @InjectMocks private VerificationTokenServiceImpl verificationTokenService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(verificationTokenService, "secret", "secret");
    ReflectionTestUtils.setField(verificationTokenService, "tokenDuration", 900);
  }

  @Test
  void createToken() {
    final var token = verificationTokenService.create(1L);
    assertThat(token).isNotNull();
  }

  @Test
  void checkRefreshToken() {
    final var token = verificationTokenService.create(1L);
    verificationTokenService.check(token);
  }
}
