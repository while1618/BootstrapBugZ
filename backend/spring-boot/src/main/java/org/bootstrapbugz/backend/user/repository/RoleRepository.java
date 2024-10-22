package org.bootstrapbugz.backend.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.backend.user.model.Role;
import org.bootstrapbugz.backend.user.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAllByNameIn(Set<RoleName> roleNames);

  Optional<Role> findByName(RoleName roleName);
}
