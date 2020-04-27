package com.app.bootstrapbugz.validator.impl;

import com.app.bootstrapbugz.repository.user.UserRepository;
import com.app.bootstrapbugz.validator.EmailExist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistImpl implements ConstraintValidator<EmailExist, String> {
    private final UserRepository userRepository;

    public EmailExistImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void initialize(EmailExist constraint) {
    }

    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}
