package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.response.UserResponse;

import java.util.List;

public interface UserService {
  List<UserResponse> findAll();

  UserResponse findByUsername(String username);
}
