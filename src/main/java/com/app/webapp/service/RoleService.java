package com.app.webapp.service;

import com.app.webapp.model.RoleName;
import com.app.webapp.model.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName name);
}
