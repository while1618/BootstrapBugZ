package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.dto.UserDto;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;

public interface UserService {
  UserDto findByUsername(String username);

  UserDto update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
