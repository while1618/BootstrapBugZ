package org.bootstrapbugz.api.user.service;

import java.util.List;

import org.bootstrapbugz.api.user.dto.SimpleUserDto;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;

public interface UserService {
  List<SimpleUserDto> findAll();

  SimpleUserDto findByUsername(String username);

  SimpleUserDto update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
