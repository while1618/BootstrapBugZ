package org.bootstrapbugz.backend.shared.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
  @Bean
  public MessageSource messageSource() {
    final var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:error-codes.properties");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
