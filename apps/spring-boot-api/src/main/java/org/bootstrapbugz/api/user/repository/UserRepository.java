package org.bootstrapbugz.api.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.QueryHint;

import org.bootstrapbugz.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface UserRepository extends JpaRepository<User, Long> {
  @QueryHints(
      @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
  @Nonnull
  List<User> findAllWithRoles();

  @Query("SELECT u FROM User u WHERE u.username IN (:usernames)")
  List<User> findAllByUsernameIn(Set<String> usernames);

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.username = :username")
  Optional<User> findByUsername(String username);

  @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
  Optional<User> findByUsernameOrEmail(String username, String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
