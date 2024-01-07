package org.bootstrapbugz.api.shared.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.bootstrapbugz.api.shared.message.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
  @Mock private MessageSource messageSource;
  @InjectMocks private MessageServiceImpl messageService;

  @Test
  void getMessage() {
    when(messageSource.getMessage("{token.invalid}", null, LocaleContextHolder.getLocale()))
        .thenReturn("Invalid token.");
    final var actualMessage = messageService.getMessage("{token.invalid}");
    assertThat(actualMessage).isEqualTo("Invalid token.");
  }
}
