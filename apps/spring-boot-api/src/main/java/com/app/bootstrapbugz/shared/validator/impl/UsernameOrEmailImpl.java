package com.app.bootstrapbugz.shared.validator.impl;

import com.app.bootstrapbugz.shared.constants.Regex;
import com.app.bootstrapbugz.shared.validator.UsernameOrEmail;
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
