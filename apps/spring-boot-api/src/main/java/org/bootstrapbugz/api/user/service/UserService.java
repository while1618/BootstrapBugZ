package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateUserRequest;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

public interface UserService {
  UserResponse findByUsername(String username);

  UserResponse update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
