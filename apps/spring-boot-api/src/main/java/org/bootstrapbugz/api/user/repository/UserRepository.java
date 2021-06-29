package org.bootstrapbugz.api.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bootstrapbugz.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
  List<User> findAllWithRoles();

  List<User> findAllByUsernameIn(Set<String> usernames);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameOrEmail(String username, String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
