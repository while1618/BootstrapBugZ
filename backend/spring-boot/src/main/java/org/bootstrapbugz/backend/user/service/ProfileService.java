package org.bootstrapbugz.backend.user.service;

import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.backend.user.payload.request.PatchProfileRequest;

public interface ProfileService {
  UserDTO find();

  UserDTO patch(PatchProfileRequest patchProfileRequest);

  void delete();

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
