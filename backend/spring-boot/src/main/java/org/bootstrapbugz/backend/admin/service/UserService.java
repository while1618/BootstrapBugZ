package org.bootstrapbugz.backend.admin.service;

import org.bootstrapbugz.backend.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.backend.admin.payload.request.UserRequest;
import org.bootstrapbugz.backend.shared.generic.crud.CrudService;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;

public interface UserService extends CrudService<UserDTO, UserRequest> {
  UserDTO patch(Long id, PatchUserRequest patchUserRequest);
}
