package org.bootstrapbugz.api.admin.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.admin.service.impl.AdminServiceImpl;
import org.bootstrapbugz.api.user.dto.UserDto;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private MessageSource messageSource;
  @Spy private UserMapperImpl userMapper;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;

  @InjectMocks private AdminServiceImpl adminService;

  private AdminRequest adminRequest;
  private ChangeRoleRequest changeRoleRequest;
  private List<User> users;
  private List<Role> roles;

  @BeforeEach
  void init() {
    adminRequest = new AdminRequest(Set.of("user"));
    changeRoleRequest =
        new ChangeRoleRequest(adminRequest.getUsernames(), Set.of(RoleName.USER, RoleName.ADMIN));
//    Role userRole = new Role(1L, RoleName.USER);
    users =
        Collections.singletonList(
            new User()
                .setId(2L)
                .setFirstName("User")
                .setLastName("User")
                .setUsername("user")
                .setEmail("user@localhost.com")
                .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
                .setActivated(true));
//                .setRoles(Set.of(userRole)));
//    roles = Arrays.asList(new Role(1L, RoleName.ADMIN), userRole);
  }

  @Test
  void findAllUsers_ok() {
    when(userRepository.findAllWithRoles()).thenReturn(users);
    List<UserDto> foundUsers = adminService.findAllUsers();
    assertThat(foundUsers).isNotNull().hasSize(1);
  }

  @Test
  void changeRole_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
//    when(roleRepository.findAllByNameIn(changeRoleRequest.getRoleNames())).thenReturn(roles);
//    LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
    adminService.changeRole(changeRoleRequest);
//    assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
    assertEquals(new HashSet<>(roles), users.get(0).getRoles());
  }

  @Test
  void lock_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
//    LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
    adminService.lock(adminRequest);
//    assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
    assertFalse(users.get(0).isNonLocked());
  }

  @Test
  void unlock_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
//    LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
    adminService.unlock(adminRequest);
//    assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
    assertTrue(users.get(0).isNonLocked());
  }

  @Test
  void activate_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
//    LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
    adminService.activate(adminRequest);
//    assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
    assertTrue(users.get(0).isActivated());
  }

  @Test
  void deactivate_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
//    LocalDateTime updatedAtBeforeChange = users.get(0).getUpdatedAt();
    adminService.deactivate(adminRequest);
//    assertNotEquals(updatedAtBeforeChange, users.get(0).getUpdatedAt());
    assertFalse(users.get(0).isActivated());
  }

  @Test
  void delete_noContent() {
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(users);
    adminService.delete(adminRequest);
    verify(userRepository, times(1)).delete(users.get(0));
  }
}
