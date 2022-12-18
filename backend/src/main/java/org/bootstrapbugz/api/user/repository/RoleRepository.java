package org.bootstrapbugz.api.user.repository;

import org.bootstrapbugz.api.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {}
