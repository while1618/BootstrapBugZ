package com.app.bootstrapbugz.user.util;

import com.app.bootstrapbugz.common.error.ErrorDomain;
import com.app.bootstrapbugz.common.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.user.model.User;
import com.app.bootstrapbugz.user.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtilities {
  private UserUtilities() {}

  public static User findLoggedUser(UserRepository userRepository, MessageSource messageSource) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository
        .findByUsername(auth.getName())
        .orElseThrow(
            () ->
                new ResourceNotFound(
                    messageSource.getMessage(
                        "user.notFound", null, LocaleContextHolder.getLocale()),
                    ErrorDomain.USER));
  }
}
