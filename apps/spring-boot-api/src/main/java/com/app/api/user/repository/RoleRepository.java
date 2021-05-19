package com.app.api.user.repository;

import com.app.api.user.model.Role;
import com.app.api.user.model.RoleName;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAllByNameIn(Set<RoleName> names);

  Optional<Role> findByName(RoleName name);
}
