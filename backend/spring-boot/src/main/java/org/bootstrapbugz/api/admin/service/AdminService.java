package org.bootstrapbugz.api.admin.service;

import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;

public interface AdminService {
  void activate(String username);

  void deactivate(String username);

  void unlock(String username);

  void lock(String username);

  void updateRole(String username, UpdateRoleRequest updateRoleRequest);

  void delete(String username);
}
