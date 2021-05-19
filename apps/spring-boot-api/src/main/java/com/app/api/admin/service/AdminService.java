package com.app.api.admin.service;

import com.app.api.admin.request.AdminRequest;
import com.app.api.admin.request.ChangeRoleRequest;
import com.app.api.user.dto.UserDto;
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
