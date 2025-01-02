package org.bugzkit.api.auth.jwt.service;

import org.bugzkit.api.user.model.User;

public interface ResetPasswordTokenService {
  String create(Long userId);

  void check(String token);

  void sendToEmail(User user, String token);
}
