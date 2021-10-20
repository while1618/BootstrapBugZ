package org.bootstrapbugz.api.user.data;

import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends DatabaseContainers {
  private final User expectedUser =
      new User(
          2L,
          "User",
          "User",
          "user",
          "user@bootstrapbugz.com",
          null,
          true,
          true,
          Collections.singleton(new Role(RoleName.USER)));

  private final Role userRole = new Role(RoleName.USER);
  private final Role adminRole = new Role(RoleName.ADMIN);
  private final User user =
      new User()
          .setFirstName("User")
          .setLastName("User")
          .setUsername("user")
          .setEmail("user@bootstrapbugz.com")
          .setPassword("qwerty123")
          .setActivated(true)
          .setRoles(Collections.singleton(userRole));
  private final User admin =
      new User()
          .setFirstName("Admin")
          .setLastName("Admin")
          .setUsername("admin")
          .setEmail("admin@bootstrapbugz.com")
          .setPassword("qwerty123")
          .setActivated(true)
          .setRoles(Set.of(userRole, adminRole));

  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;

  @BeforeAll
  void setUp() {
    roleRepository.saveAll(List.of(userRole, adminRole));
    userRepository.saveAll(List.of(user, admin));
  }

  @AfterAll
  void cleanUp() {
    userRepository.deleteAll(List.of(user, admin));
    roleRepository.deleteAll(List.of(userRole, adminRole));
  }

  @Test
  void itShouldFindAllUsersWithRoles() {
    assertThat(userRepository.findAllWithRoles().size()).isEqualTo(2);
  }

  @Test
  void itShouldFindAllUsersByUsernameIn() {
    assertThat(userRepository.findAllByUsernameIn(Set.of("user", "admin")).size()).isEqualTo(2);
  }

  @Test
  void itShouldFindUserByEmail() {
    var actualUser = userRepository.findByEmail("user@bootstrapbugz.com").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .ignoringFields("id")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsername() {
    var actualUser = userRepository.findByUsername("user").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .ignoringFields("id")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsernameOrEmail() {
    var actualUser = userRepository.findByUsernameOrEmail("user", "user").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .ignoringFields("id")
        .isEqualTo(expectedUser);
  }

  @Test
  void userShouldExistsByEmail() {
    assertThat(userRepository.existsByEmail("user@bootstrapbugz.com")).isTrue();
  }

  @Test
  void userShouldExistsByUsername() {
    assertThat(userRepository.existsByUsername("user")).isTrue();
  }
}
