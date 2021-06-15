package org.bootstrapbugz.api.user.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
  @Autowired private UserRepository userRepository;

  @Test
  void itShouldFindAllUsersWithRoles() {
    Set<Role> roles = Collections.singleton(new Role(RoleName.USER));
    User expectedUser =
        new User(2L, "User", "User", "user", "decrescendo807@gmail.com", null, true, true, roles);
    List<User> actualUsers = userRepository.findAllWithRoles();
    assertThat(actualUsers.size()).isEqualTo(4);
    assertThat(actualUsers.get(1))
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindAllUsersByUsernameIn() {
    Set<Role> roles = Collections.singleton(new Role(RoleName.USER));
    User expectedUser =
        new User(2L, "User", "User", "user", "decrescendo807@gmail.com", null, true, true, roles);
    List<User> actualUsers = userRepository.findAllByUsernameIn(Collections.singleton("user"));
    assertThat(actualUsers.size()).isEqualTo(1);
    assertThat(actualUsers.get(0))
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByEmail() {
    Set<Role> roles = Collections.singleton(new Role(RoleName.USER));
    User expectedUser =
        new User(2L, "User", "User", "user", "decrescendo807@gmail.com", null, true, true, roles);
    User actualUser = userRepository.findByEmail("decrescendo807@gmail.com").orElseThrow();
    assertThat(actualUser)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsername() {
    Set<Role> roles = Collections.singleton(new Role(RoleName.USER));
    User expectedUser =
      new User(2L, "User", "User", "user", "decrescendo807@gmail.com", null, true, true, roles);
    User actualUser = userRepository.findByUsername("user").orElseThrow();
    assertThat(actualUser)
      .usingRecursiveComparison()
      .ignoringFields("password")
      .isEqualTo(expectedUser);
  }

  @Test
  void itShouldFindUserByUsernameOrEmail() {
    Set<Role> roles = Collections.singleton(new Role(RoleName.USER));
    User expectedUser =
      new User(2L, "User", "User", "user", "decrescendo807@gmail.com", null, true, true, roles);
    User actualUser = userRepository.findByUsernameOrEmail("user", "user").orElseThrow();
    assertThat(actualUser)
      .usingRecursiveComparison()
      .ignoringFields("password")
      .isEqualTo(expectedUser);
  }

  @Test
  void userShouldExistsByEmail() {
    boolean exists = userRepository.existsByEmail("decrescendo807@gmail.com");
    assertThat(exists).isTrue();
  }

  @Test
  void userShouldExistsByUsername() {
    boolean exists = userRepository.existsByUsername("user");
    assertThat(exists).isTrue();
  }
}
