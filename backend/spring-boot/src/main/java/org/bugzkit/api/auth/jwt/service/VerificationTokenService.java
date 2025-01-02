package org.bugzkit.api.auth.jwt.service;

import org.bugzkit.api.user.model.User;

public interface VerificationTokenService {
  String create(Long userId);

  void check(String token);

  void sendToEmail(User user, String token);
}
