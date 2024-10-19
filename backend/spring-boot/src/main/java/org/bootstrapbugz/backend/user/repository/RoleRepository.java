package org.bootstrapbugz.backend.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.backend.user.model.Role;
import org.bootstrapbugz.backend.user.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAllByNameIn(Set<RoleName> roleNames);

  @Query("select r from Role r join r.users u where u.id = :userId")
  Set<Role> findRolesByUserId(Long userId);

  Optional<Role> findByName(RoleName roleName);
}
