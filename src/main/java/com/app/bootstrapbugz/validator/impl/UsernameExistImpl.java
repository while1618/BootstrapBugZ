package com.app.bootstrapbugz.validator.impl;

import com.app.bootstrapbugz.repository.user.UserRepository;
import com.app.bootstrapbugz.validator.UsernameExist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameExistImpl implements ConstraintValidator<UsernameExist, String> {
    private final UserRepository userRepository;

    public UsernameExistImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void initialize(UsernameExist constraint) {
    }

    public boolean isValid(String username, ConstraintValidatorContext context) {
        return !userRepository.existsByUsername(username);
    }
}
