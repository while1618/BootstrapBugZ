package org.bootstrapbugz.api.user.service;

import java.util.List;
import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {
  List<UserDTO> findAll(Pageable pageable);

  UserDTO findById(Long id);

  UserDTO findByUsername(String username);

  AvailabilityDTO usernameAvailability(UsernameAvailabilityRequest usernameAvailabilityRequest);

  AvailabilityDTO emailAvailability(EmailAvailabilityRequest emailAvailabilityRequest);
}
