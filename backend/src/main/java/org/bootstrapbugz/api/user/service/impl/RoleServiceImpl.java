package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;
  private final MessageService messageService;

  public RoleServiceImpl(RoleRepository roleRepository, MessageService messageService) {
    this.roleRepository = roleRepository;
    this.messageService = messageService;
  }

  @Override
  public List<Role> findAllByNameIn(Set<RoleName> roleNames) {
    return roleRepository.findAllByNameIn(roleNames);
  }

  @Override
  public Role findByName(RoleName roleName) {
    return roleRepository
        .findByName(roleName)
        .orElseThrow(
            () -> new ResourceNotFoundException(messageService.getMessage("role.notFound")));
  }
}
