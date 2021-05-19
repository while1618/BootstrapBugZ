package com.app.api.shared.validator.impl;

import com.app.api.shared.validator.EmailExist;
import com.app.api.user.repository.UserRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistImpl implements ConstraintValidator<EmailExist, String> {
  private final UserRepository userRepository;

  public EmailExistImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isValid(String email, ConstraintValidatorContext context) {
    return !userRepository.existsByEmail(email);
  }
}
