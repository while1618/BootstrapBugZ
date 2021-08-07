package org.bootstrapbugz.api.admin.service;

import java.util.List;
import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.user.response.UserResponse;

public interface AdminService {
  List<UserResponse> findAllUsers();

  void changeRole(ChangeRoleRequest changeRoleRequest);

  void lock(AdminRequest adminRequest);

  void unlock(AdminRequest adminRequest);

  void activate(AdminRequest adminRequest);

  void deactivate(AdminRequest adminRequest);

  void delete(AdminRequest adminRequest);
}
