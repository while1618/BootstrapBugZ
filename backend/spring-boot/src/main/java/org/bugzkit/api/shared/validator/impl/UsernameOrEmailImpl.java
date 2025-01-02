package org.bugzkit.api.shared.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.bugzkit.api.shared.constants.Regex;
import org.bugzkit.api.shared.validator.UsernameOrEmail;

public class UsernameOrEmailImpl implements ConstraintValidator<UsernameOrEmail, String> {
  public boolean isValid(String usernameOrEmail, ConstraintValidatorContext context) {
    return usernameOrEmail != null
        && !usernameOrEmail.isEmpty()
        && (Pattern.compile(Regex.USERNAME).matcher(usernameOrEmail).matches()
            || Pattern.compile(Regex.EMAIL).matcher(usernameOrEmail).matches());
  }
}
