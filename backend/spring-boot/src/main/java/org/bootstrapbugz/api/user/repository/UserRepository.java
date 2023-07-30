package org.bootstrapbugz.api.user.repository;

import java.util.Optional;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
  Page<User> findAllWithRoles(Pageable pageable);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
  Optional<User> findByIdWithRoles(Long id);

  Optional<User> findByUsername(String username);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
  Optional<User> findByUsernameWithRoles(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsernameOrEmail(String username, String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
