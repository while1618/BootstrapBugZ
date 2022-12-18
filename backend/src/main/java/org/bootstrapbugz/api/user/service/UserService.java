package org.bootstrapbugz.api.user.service;

import java.util.List;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

public interface UserService {
  List<UserResponse> findAll();

  UserResponse findByUsername(String username);
}
