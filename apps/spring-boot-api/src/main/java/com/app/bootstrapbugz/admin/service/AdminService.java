package com.app.bootstrapbugz.admin.service;

import com.app.bootstrapbugz.admin.request.AdminRequest;
import com.app.bootstrapbugz.admin.request.ChangeRoleRequest;
import com.app.bootstrapbugz.user.dto.UserDto;
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
