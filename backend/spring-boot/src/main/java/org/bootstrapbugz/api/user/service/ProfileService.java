package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;

public interface ProfileService {
  UserDTO find();

  UserDTO patch(UpdateProfileRequest updateProfileRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);

  void delete();
}
