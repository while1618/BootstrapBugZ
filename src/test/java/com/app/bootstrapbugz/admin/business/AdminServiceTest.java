package com.app.bootstrapbugz.admin.business;

import com.app.bootstrapbugz.admin.dto.request.AdminRequest;
import com.app.bootstrapbugz.admin.dto.request.ChangeRoleRequest;
import com.app.bootstrapbugz.user.model.Role;
import com.app.bootstrapbugz.user.model.RoleName;
import com.app.bootstrapbugz.user.model.User;
import com.app.bootstrapbugz.user.repository.RoleRepository;
import com.app.bootstrapbugz.user.repository.UserRepository;
import com.app.bootstrapbugz.admin.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private AdminRequest adminRequest;
    private ChangeRoleRequest changeRoleRequest;
    private List<User> users;
    private List<Role> roles;

    @BeforeEach
    void init() {
        adminRequest = new AdminRequest(Set.of("user"));
        changeRoleRequest = new ChangeRoleRequest(
                adminRequest.getUsernames(),
                Set.of(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        Role userRole = new Role(1L, RoleName.ROLE_USER);
        users = Collections.singletonList(new User()
                .setId(2L)
                .setFirstName("User")
                .setLastName("User")
                .setUsername("user")
                .setEmail("user@localhost.com")
                .setPassword(bCryptPasswordEncoder.encode("123"))
                .setActivated(true)
                .setRoles(Set.of(userRole)));
        roles = Arrays.asList(new Role(1L, RoleName.ROLE_ADMIN), userRole);
    }

    @Test
    void logoutUsersFromAllDevices_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        LocalDateTime beforeLogout = users.get(0).getLogoutFromAllDevicesAt();
        adminService.logoutUsersFromAllDevices(adminRequest);
        assertNotEquals(beforeLogout, users.get(0).getLogoutFromAllDevicesAt());
    }

    @Test
    void changeUsersRole_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        when(roleRepository.findAllByNameIn(changeRoleRequest.getRoleNames())).thenReturn(roles);
        LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
        adminService.changeUsersRole(changeRoleRequest);
        assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
        assertEquals(new HashSet<>(roles), users.get(0).getRoles());
    }

    @Test
    void lockUsers_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
        adminService.lockUsers(adminRequest);
        assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
        assertFalse(users.get(0).isNonLocked());
    }

    @Test
    void unlockUsers_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
        adminService.unlockUsers(adminRequest);
        assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
        assertTrue(users.get(0).isNonLocked());
    }

    @Test
    void activateUsers_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
        adminService.activateUser(adminRequest);
        assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
        assertTrue(users.get(0).isActivated());
    }

    @Test
    void deactivateUsers_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
        adminService.deactivateUser(adminRequest);
        assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
        assertFalse(users.get(0).isActivated());
    }

    @Test
    void deleteUsers_ok() {
        when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
        adminService.deleteUsers(adminRequest);
        verify(userRepository, times(1)).deleteAll(users);
    }
}
