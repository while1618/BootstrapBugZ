package com.app.webapp.validator.impl;

import com.app.webapp.dto.request.SignUpRequest;
import com.app.webapp.validator.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesImpl implements ConstraintValidator<PasswordMatches, SignUpRequest> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext constraintValidatorContext) {
        return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());
    }
}
