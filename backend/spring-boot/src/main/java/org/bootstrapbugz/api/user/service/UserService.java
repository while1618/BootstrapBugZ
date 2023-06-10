package org.bootstrapbugz.api.user.service;

import java.util.List;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

public interface UserService {
  List<UserDTO> findAll();

  UserDTO findById(Long id);

  UserDTO findByUsername(String username);
}
