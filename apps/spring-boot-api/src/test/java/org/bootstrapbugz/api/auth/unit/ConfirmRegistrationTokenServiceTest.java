package org.bootstrapbugz.api.auth.unit;

import org.bootstrapbugz.api.auth.jwt.service.impl.ConfirmRegistrationTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConfirmRegistrationTokenServiceTest {
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(confirmRegistrationTokenService, "secret", "secret");
  }

  @Test
  void itShouldCreateToken() {
    String token = confirmRegistrationTokenService.create(1L);
    assertThat(token).isNotNull();
  }

  @Test
  void itShouldCheckRefreshToken() {
    String token = confirmRegistrationTokenService.create(1L);
    confirmRegistrationTokenService.check(token);
  }
}
