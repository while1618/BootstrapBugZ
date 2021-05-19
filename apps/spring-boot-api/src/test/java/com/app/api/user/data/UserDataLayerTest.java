package com.app.api.user.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.app.api.user.model.Role;
import com.app.api.user.model.RoleName;
import com.app.api.user.model.User;
import com.app.api.user.repository.RoleRepository;
import com.app.api.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserDataLayerTest {
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void findAll() {
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(4);
  }

  @Test
  void findAllByUsernameIn() {
    Set<String> usernames = Set.of("user", "admin");
    List<User> users = userRepository.findAllByUsernameIn(usernames);
    assertThat(users).hasSize(2);
  }

  @Test
  void findByEmail() {
    User user = userRepository.findByEmail("skill.potion21@gmail.com").orElseThrow();
    assertEquals("admin", user.getUsername());
  }

  @Test
  void findByUsername() {
    User user = userRepository.findByUsername("admin").orElseThrow();
    assertEquals("Admin", user.getFirstName());
  }

  @Test
  void existsByEmail() {
    boolean found = userRepository.existsByEmail("skill.potion21@gmail.com");
    assertTrue(found);
  }

  @Test
  void existsByUsername() {
    boolean found = userRepository.existsByUsername("admin");
    assertTrue(found);
  }

  @Test
  void save() {
    Set<RoleName> names = Set.of(RoleName.USER, RoleName.ADMIN);
    List<Role> roles = roleRepository.findAllByNameIn(names);
    User user =
        new User()
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost.com")
            .setPassword("qwerty123")
            .setRoles(new HashSet<>(roles));
    userRepository.save(user);
    flushAndClear();
    User userFromDB = userRepository.findByUsername("test").orElseThrow();
    assertEquals(user, userFromDB);
  }

  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void saveAll() {
    Set<RoleName> names = Set.of(RoleName.USER, RoleName.ADMIN);
    List<Role> roles = roleRepository.findAllByNameIn(names);
    User user1 =
        new User()
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost.com")
            .setPassword("qwerty123")
            .setRoles(new HashSet<>(roles));
    User user2 =
        new User()
            .setFirstName("Test2")
            .setLastName("Test2")
            .setUsername("test2")
            .setEmail("test2@localhost.com")
            .setPassword("qwerty123")
            .setRoles(new HashSet<>(roles));
    userRepository.saveAll(Set.of(user1, user2));
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(6);
  }

  @Test
  void deleteAll() {
    List<User> users = userRepository.findAllByUsernameIn(Set.of("user", "not_activated"));
    users.forEach(user -> userRepository.delete(user));
    assertThat(userRepository.findAll()).hasSize(2);
    assertThat(roleRepository.findAll()).hasSize(2);
  }
}
