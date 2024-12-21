package org.bootstrapbugz.api.user.service;

import java.util.List;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;

public interface RoleService {
  List<RoleDTO> findAll();
}
