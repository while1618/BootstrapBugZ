package com.app.bootstrapbugz.user.service;

import com.app.bootstrapbugz.user.dto.SimpleUserDto;
import com.app.bootstrapbugz.user.request.ChangePasswordRequest;
import com.app.bootstrapbugz.user.request.UpdateUserRequest;
import java.util.List;

public interface UserService {
  List<SimpleUserDto> findAll();

  SimpleUserDto findByUsername(String username);

  SimpleUserDto update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
