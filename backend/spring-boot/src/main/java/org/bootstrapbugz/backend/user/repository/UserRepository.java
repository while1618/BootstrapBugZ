package org.bootstrapbugz.backend.user.repository;

import java.util.Optional;
import org.bootstrapbugz.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  @EntityGraph(attributePaths = "roles")
  Page<User> findAll(Pageable pageable);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findWithRolesById(Long id);

  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findWithRolesByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsernameOrEmail(String username, String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
