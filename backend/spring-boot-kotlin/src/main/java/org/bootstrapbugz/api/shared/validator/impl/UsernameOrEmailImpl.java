package org.bootstrapbugz.api.shared.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;

public class UsernameOrEmailImpl implements ConstraintValidator<UsernameOrEmail, String> {
  public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
    return Pattern.compile(Regex.USERNAME).matcher(usernameOrEmail).matches()
        || Pattern.compile(Regex.EMAIL).matcher(usernameOrEmail).matches();
  }
}
