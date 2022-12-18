package org.bootstrapbugz.api.auth.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.bootstrapbugz.api.auth.validator.EmailExist;
import org.bootstrapbugz.api.user.repository.UserRepository;

public class EmailExistImpl implements ConstraintValidator<EmailExist, String> {
  private final UserRepository userRepository;

  public EmailExistImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isValid(String email, ConstraintValidatorContext context) {
    return !userRepository.existsByEmail(email);
  }
}
