package com.app.webapp.validator.impl;

import com.app.webapp.repository.UserRepository;
import com.app.webapp.validator.UniqueUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameImpl implements ConstraintValidator<UniqueUsername, String> {
   private final UserRepository userRepository;

   public UniqueUsernameImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   public void initialize(UniqueUsername constraint) {
   }

   public boolean isValid(String username, ConstraintValidatorContext context) {
      return !userRepository.existsByUsername(username);
   }
}
