package org.bootstrapbugz.api.user.service;

import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.payload.response.UserResponse;

public interface ProfileService {
  UserResponse update(UpdateProfileRequest updateProfileRequest);

  void changePassword(ChangePasswordRequest changePasswordRequest);
}
