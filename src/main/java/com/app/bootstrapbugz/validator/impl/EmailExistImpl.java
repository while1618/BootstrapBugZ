package com.app.bootstrapbugz.validator.impl;

import com.app.bootstrapbugz.user.repository.UserRepository;
import com.app.bootstrapbugz.validator.EmailExist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistImpl implements ConstraintValidator<EmailExist, String> {
    private final UserRepository userRepository;

    public EmailExistImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(EmailExist constraint) {
        // it must be overridden but there is no need to put code in it
    }

    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}
