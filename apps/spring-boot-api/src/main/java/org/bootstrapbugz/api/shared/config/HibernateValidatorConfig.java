package org.bootstrapbugz.api.shared.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class HibernateValidatorConfig {
  @Bean
  public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
    return Validation.byProvider(HibernateValidator.class)
        .configure()
        .constraintValidatorFactory(
            new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
        .buildValidatorFactory()
        .getValidator();
  }
}
