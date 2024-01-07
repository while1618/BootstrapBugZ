package org.bootstrapbugz.api.auth.jwt.service;

import org.bootstrapbugz.api.user.model.User;

public interface VerificationTokenService {
  String create(Long userId);

  void check(String token);

  void sendToEmail(User user, String token);
}
