package com.app.webapp.service.impl;

import com.app.webapp.model.RoleName;
import com.app.webapp.model.Role;
import com.app.webapp.repository.RoleRepository;
import com.app.webapp.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleRepository.findByName(name);
    }
}
