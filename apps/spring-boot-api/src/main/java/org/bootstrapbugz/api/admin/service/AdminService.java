package org.bootstrapbugz.api.admin.service;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.user.dto.UserDto;
import java.util.List;

public interface AdminService {
  List<UserDto> findAllUsers();

  void changeRole(ChangeRoleRequest changeRoleRequest);

  void lock(AdminRequest adminRequest);

  void unlock(AdminRequest adminRequest);

  void activate(AdminRequest adminRequest);

  void deactivate(AdminRequest adminRequest);

  void delete(AdminRequest adminRequest);
}
