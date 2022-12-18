package org.bootstrapbugz.api.admin.service;

import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;

public interface AdminService {
  void activate(AdminRequest adminRequest);

  void deactivate(AdminRequest adminRequest);

  void unlock(AdminRequest adminRequest);

  void lock(AdminRequest adminRequest);

  void updateRole(UpdateRoleRequest updateRoleRequest);

  void delete(AdminRequest adminRequest);
}
