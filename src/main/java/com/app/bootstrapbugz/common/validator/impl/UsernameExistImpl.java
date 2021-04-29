package com.app.bootstrapbugz.common.validator.impl;

import com.app.bootstrapbugz.common.validator.UsernameExist;
import com.app.bootstrapbugz.user.repository.UserRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameExistImpl implements ConstraintValidator<UsernameExist, String> {
  private final UserRepository userRepository;

  public UsernameExistImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isValid(String username, ConstraintValidatorContext context) {
    return !userRepository.existsByUsername(username);
  }
}
