package com.app.bootstrapbugz.validator.impl;

import com.app.bootstrapbugz.user.repository.UserRepository;
import com.app.bootstrapbugz.validator.UsernameExist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameExistImpl implements ConstraintValidator<UsernameExist, String> {
    private final UserRepository userRepository;

    public UsernameExistImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UsernameExist constraint) {
        // it must be overridden but there is no need to put code in it
    }

    public boolean isValid(String username, ConstraintValidatorContext context) {
        return !userRepository.existsByUsername(username);
    }
}
