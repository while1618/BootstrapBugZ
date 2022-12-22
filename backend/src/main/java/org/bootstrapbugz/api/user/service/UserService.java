package org.bootstrapbugz.api.user.service;

import java.util.List;
import org.bootstrapbugz.api.user.payload.dto.UserDto;

public interface UserService {
  List<UserDto> findAll();

  UserDto findByUsername(String username);
}
