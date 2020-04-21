package com.app.webapp.repository.user;

import com.app.webapp.model.user.RoleName;
import com.app.webapp.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
