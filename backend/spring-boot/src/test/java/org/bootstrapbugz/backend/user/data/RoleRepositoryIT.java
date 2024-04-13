package org.bootstrapbugz.backend.user.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.bootstrapbugz.backend.shared.config.DatabaseContainers;
import org.bootstrapbugz.backend.user.model.Role;
import org.bootstrapbugz.backend.user.model.Role.RoleName;
import org.bootstrapbugz.backend.user.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryIT extends DatabaseContainers {
  @Autowired private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    roleRepository.save(new Role(RoleName.ADMIN));
    roleRepository.save(new Role(RoleName.USER));
  }

  @AfterEach
  void cleanUp() {
    roleRepository.deleteAll();
  }

  @Test
  void findAllByNameIn() {
    final var roles = roleRepository.findAllByNameIn(Set.of(RoleName.USER, RoleName.ADMIN));
    assertThat(roles).hasSize(2);
  }

  @Test
  void findByName() {
    assertThat(roleRepository.findByName(RoleName.USER)).isPresent();
  }
}
