package org.bootstrapbugz.backend.user.repository;

import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.backend.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u.id FROM User u")
  List<Long> findUserIds(Pageable pageable);

  @EntityGraph(attributePaths = "roles")
  List<User> findByIdIn(List<Long> ids);

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
