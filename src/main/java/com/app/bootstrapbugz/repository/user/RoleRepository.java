package com.app.bootstrapbugz.repository.user;

import com.app.bootstrapbugz.model.user.Role;
import com.app.bootstrapbugz.model.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByNameIn(List<RoleName> names);

    Optional<Role> findByName(RoleName name);
}
