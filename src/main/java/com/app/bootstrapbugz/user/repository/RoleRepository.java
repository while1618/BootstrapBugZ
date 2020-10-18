package com.app.bootstrapbugz.user.repository;

import com.app.bootstrapbugz.user.model.Role;
import com.app.bootstrapbugz.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByNameIn(List<RoleName> names);

    Optional<Role> findByName(RoleName name);
}
