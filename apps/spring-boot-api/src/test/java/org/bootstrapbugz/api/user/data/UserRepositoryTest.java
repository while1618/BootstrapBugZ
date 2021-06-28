package org.bootstrapbugz.api.user.data;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.Set;

import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
  @Autowired private UserRepository userRepository;

  private final User expectedUser =
      new User(
          2L,
          "User",
          "User",
          "user",
          "user@localhost.com",
          null,
          true,
          true,
          Collections.singleton(new Role(RoleName.USER)));

  @Test
  void itShouldFindAllUsersWithRoles() {
    var actualUsers = userRepository.findAllWithRoles();
    assertThat(actualUsers.size()).isEqualTo(7);
    assertThat(actualUsers.get(1))
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindAllUsersByUsernameIn() {
    var actualUsers = userRepository.findAllByUsernameIn(Set.of("user", "admin"));
    assertThat(actualUsers.size()).isEqualTo(2);
    assertThat(actualUsers.get(1))
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByEmail() {
    var actualUser = userRepository.findByEmail("user@localhost.com").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsername() {
    var actualUser = userRepository.findByUsername("user").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsernameOrEmail() {
    var actualUser = userRepository.findByUsernameOrEmail("user", "user").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void userShouldExistsByEmail() {
    assertThat(userRepository.existsByEmail("user@localhost.com")).isTrue();
  }

  @Test
  void userShouldExistsByUsername() {
    assertThat(userRepository.existsByUsername("user")).isTrue();
  }
}
