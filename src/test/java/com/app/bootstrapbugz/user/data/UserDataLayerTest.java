package com.app.bootstrapbugz.user.data;

import com.app.bootstrapbugz.model.user.Role;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.repository.user.RoleRepository;
import com.app.bootstrapbugz.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserDataLayerTest {
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
    void whenDbInitialized_findData() {
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();
        assertThat(users).isNotEmpty();
        assertThat(roles).isNotEmpty();
        assertThat(users).hasSize(4);
        assertThat(roles).hasSize(2);
    }
}
