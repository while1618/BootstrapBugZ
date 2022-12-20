package org.bootstrapbugz.api.user.service;

import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;

public interface RoleService {
  List<Role> findAllByNameIn(Set<RoleName> roleNames);

  Role findByName(RoleName roleName);
}
