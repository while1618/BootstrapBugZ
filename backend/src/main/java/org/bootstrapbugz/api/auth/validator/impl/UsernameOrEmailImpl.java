package org.bootstrapbugz.api.auth.validator.impl;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;
import org.bootstrapbugz.api.auth.validator.UsernameOrEmail;
import org.bootstrapbugz.api.shared.constants.Regex;

public class UsernameOrEmailImpl implements ConstraintValidator<UsernameOrEmail, String> {
  public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
    return Pattern.compile(Regex.USERNAME).matcher(usernameOrEmail).matches()
        || EmailValidator.getInstance().isValid(usernameOrEmail);
  }
}
