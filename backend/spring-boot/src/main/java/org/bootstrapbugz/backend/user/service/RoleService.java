package org.bootstrapbugz.backend.user.service;

import java.util.List;
import org.bootstrapbugz.backend.user.payload.dto.RoleDTO;

public interface RoleService {
  List<RoleDTO> findAll();
}
