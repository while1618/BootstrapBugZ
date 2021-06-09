package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.response.UserResponse;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;

public interface UserService {
  UserResponse findByUsername(String username);

  UserResponse update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
