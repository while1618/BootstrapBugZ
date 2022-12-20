package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

public interface ProfileService {
  UserDTO update(UpdateProfileRequest updateProfileRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
