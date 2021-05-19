package com.app.api.user.service;

import com.app.api.user.dto.SimpleUserDto;
import com.app.api.user.request.ChangePasswordRequest;
import com.app.api.user.request.UpdateUserRequest;
import java.util.List;

public interface UserService {
  List<SimpleUserDto> findAll();

  SimpleUserDto findByUsername(String username);

  SimpleUserDto update(UpdateUserRequest updateUserRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
