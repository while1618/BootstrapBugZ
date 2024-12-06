package org.bootstrapbugz.backend.user.service.impl;

import java.util.List;
import org.bootstrapbugz.backend.user.mapper.UserMapper;
import org.bootstrapbugz.backend.user.payload.dto.RoleDTO;
import org.bootstrapbugz.backend.user.repository.RoleRepository;
import org.bootstrapbugz.backend.user.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;

  public RoleServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public List<RoleDTO> findAll() {
    return roleRepository.findAll().stream().map(UserMapper.INSTANCE::roleToRoleDTO).toList();
  }
}
