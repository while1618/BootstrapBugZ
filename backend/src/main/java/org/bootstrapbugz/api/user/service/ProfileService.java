package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.dto.UserDto;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;

public interface ProfileService {
  UserDto update(UpdateProfileRequest updateProfileRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
