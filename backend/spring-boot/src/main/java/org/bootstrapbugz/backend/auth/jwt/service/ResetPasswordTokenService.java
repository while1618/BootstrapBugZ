package org.bootstrapbugz.backend.auth.jwt.service;

import org.bootstrapbugz.backend.user.model.User;

public interface ResetPasswordTokenService {
  String create(Long userId);

  void check(String token);

  void sendToEmail(User user, String token);
}
