package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.dto.SimpleUserDto;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import java.util.List;

public interface UserService {
  List<SimpleUserDto> findAll();

  SimpleUserDto findByUsername(String username);

  SimpleUserDto update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
