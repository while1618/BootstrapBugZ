package org.bootstrapbugz.api.auth.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.bootstrapbugz.api.auth.validator.UsernameOrEmail;
import org.bootstrapbugz.api.shared.constants.Regex;

public class UsernameOrEmailImpl implements ConstraintValidator<UsernameOrEmail, String> {
  public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
    return Pattern.compile(Regex.USERNAME).matcher(usernameOrEmail).matches()
        || Pattern.compile(Regex.EMAIL).matcher(usernameOrEmail).matches();
  }
}
