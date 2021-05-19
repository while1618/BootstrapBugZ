package org.bootstrapbugz.api.shared.validator.impl;

import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

public class UsernameOrEmailImpl implements ConstraintValidator<UsernameOrEmail, String> {
  public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
    return Pattern.compile(Regex.USERNAME).matcher(usernameOrEmail).matches()
        || EmailValidator.getInstance().isValid(usernameOrEmail);
  }
}
