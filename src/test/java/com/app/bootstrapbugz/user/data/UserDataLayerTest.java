package com.app.bootstrapbugz.user.data;

import com.app.bootstrapbugz.user.model.Role;
import com.app.bootstrapbugz.user.model.RoleName;
import com.app.bootstrapbugz.user.model.User;
import com.app.bootstrapbugz.user.repository.RoleRepository;
import com.app.bootstrapbugz.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserDataLayerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(userRepository).isNotNull();
        assertThat(roleRepository).isNotNull();
    }

    @Test
    void findAllUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(4);
    }

    @Test
    void findAllUsersByUsernameIn() {
        Set<String> usernames = Set.of("user", "admin");
        List<User> users = userRepository.findAllByUsernameIn(usernames);
        assertThat(users).hasSize(2);
    }

    @Test
    void findUserByEmail() {
        User user = userRepository.findByEmail("skill.potion21@gmail.com").orElseThrow();
        assertEquals("admin", user.getUsername());
    }

    @Test
    void findUserByUsername() {
        User user = userRepository.findByUsername("admin").orElseThrow();
        assertEquals("Admin", user.getFirstName());
    }

    @Test
    void userExistsByEmail() {
        boolean found = userRepository.existsByEmail("skill.potion21@gmail.com");
        assertTrue(found);
    }

    @Test
    void userExistsByUsername() {
        boolean found = userRepository.existsByUsername("admin");
        assertTrue(found);
    }

    @Test
    void findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        assertThat(roles).hasSize(2);
    }

    @Test
    void findAllRolesByNameIn() {
        Set<RoleName> names = Set.of(RoleName.ROLE_USER, RoleName.ROLE_ADMIN);
        List<Role> roles = roleRepository.findAllByNameIn(names);
        assertThat(roles).hasSize(2);
    }

    @Test
    void findRoleByName() {
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();
        assertEquals(1L, role.getId());
    }
}
