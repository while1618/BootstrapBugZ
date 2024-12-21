package org.bootstrapbugz.api.admin.service;

import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.shared.generic.crud.CrudService;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;

public interface UserService extends CrudService<UserDTO, UserRequest> {
  UserDTO patch(Long id, PatchUserRequest patchUserRequest);
}
