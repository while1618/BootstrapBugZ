package org.bootstrapbugz.backend.user.service;

import org.bootstrapbugz.backend.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.backend.shared.payload.dto.FindAllDTO;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.backend.user.payload.request.UsernameAvailabilityRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {
  FindAllDTO<UserDTO> findAll(Pageable pageable);

  UserDTO findById(Long id);

  UserDTO findByUsername(String username);

  AvailabilityDTO usernameAvailability(UsernameAvailabilityRequest usernameAvailabilityRequest);

  AvailabilityDTO emailAvailability(EmailAvailabilityRequest emailAvailabilityRequest);
}
