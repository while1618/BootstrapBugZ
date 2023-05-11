package org.bootstrapbugz.api.shared.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bootstrapbugz.api.shared.validator.UsernameExist;
import org.bootstrapbugz.api.user.repository.UserRepository;

public class UsernameExistImpl implements ConstraintValidator<UsernameExist, String> {
  private final UserRepository userRepository;

  public UsernameExistImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isValid(String username, ConstraintValidatorContext context) {
    return !userRepository.existsByUsername(username);
  }
}
