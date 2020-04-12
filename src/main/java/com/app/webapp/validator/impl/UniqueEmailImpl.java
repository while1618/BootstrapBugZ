package com.app.webapp.validator.impl;

import com.app.webapp.repository.UserRepository;
import com.app.webapp.validator.UniqueEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailImpl implements ConstraintValidator<UniqueEmail, String> {
   private final UserRepository userRepository;

   public UniqueEmailImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   public void initialize(UniqueEmail constraint) {
   }

   public boolean isValid(String email, ConstraintValidatorContext context) {
      return !userRepository.existsByEmail(email);
   }
}
