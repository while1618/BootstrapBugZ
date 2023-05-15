package org.bootstrapbugz.api.admin.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.admin.service.impl.AdminServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @InjectMocks private AdminServiceImpl adminService;
  @Captor private ArgumentCaptor<Iterable<User>> userArgumentCaptor;

  @Test
  void updateUsersRoles() {
    final var updateRolesRequest =
        new UpdateRoleRequest(Collections.singleton("test"), Set.of(RoleName.USER, RoleName.ADMIN));
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(true)
            .setRoles(Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)));
    when(userRepository.findAllByUsernameIn(updateRolesRequest.usernames()))
        .thenReturn(List.of(TestUtil.getTestUser()));
    when(roleRepository.findAllByNameIn(updateRolesRequest.roleNames()))
        .thenReturn(List.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)));
    adminService.updateRole(updateRolesRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void lockUsers() {
    final var adminRequest = new AdminRequest(Collections.singleton("test"));
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(true)
            .setNonLocked(false)
            .setRoles(Set.of(new Role(RoleName.USER)));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(TestUtil.getTestUser()));
    adminService.lock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void unlockUsers() {
    final var adminRequest = new AdminRequest(Collections.singleton("test"));
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(true)
            .setNonLocked(true)
            .setRoles(Set.of(new Role(RoleName.USER)));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(TestUtil.getTestUser()));
    adminService.unlock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void activateUsers() {
    final var adminRequest = new AdminRequest(Collections.singleton("test2"));
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(true)
            .setRoles(Set.of(new Role(RoleName.USER)));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(TestUtil.getTestUser()));
    adminService.activate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void deactivateUsers() {
    final var adminRequest = new AdminRequest(Collections.singleton("test"));
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(false)
            .setRoles(Set.of(new Role(RoleName.USER)));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(TestUtil.getTestUser()));
    adminService.deactivate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void deleteUsers() {
    final var adminRequest = new AdminRequest(Set.of("admin", "test"));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(TestUtil.getTestUser(), TestUtil.getAdminUser()));
    adminService.delete(adminRequest);
    verify(userRepository, times(2)).delete(any(User.class));
  }
}
