package org.bootstrapbugz.api.user.repository;

import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.RoleName;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAllByNameIn(Set<RoleName> names);

  Optional<Role> findByName(RoleName name);
}
