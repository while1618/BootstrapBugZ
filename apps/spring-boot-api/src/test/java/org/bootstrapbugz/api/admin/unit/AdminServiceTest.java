package org.bootstrapbugz.api.admin.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.admin.service.impl.AdminServiceImpl;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.response.RoleResponse;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;

  @InjectMocks private AdminServiceImpl adminService;

  private final Set<Role> userRoles = Set.of(new Role(RoleName.USER));
  private final Set<Role> adminRoles = Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN));
  private final User admin =
      new User(1L, "Admin", "Admin", "admin", "admin@admin.com", null, true, true, adminRoles);
  private final User user =
      new User(2L, "Test", "Test", "test", "test@test.com", null, true, true, userRoles);
  private final User restricted =
      new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, false, false, userRoles);

  @Captor private ArgumentCaptor<Iterable<User>> userArgumentCaptor;

  @Test
  void itShouldFindAllUsersWithRoles() {
    Set<RoleResponse> userRoles = Set.of(new RoleResponse(RoleName.USER.name()));
    Set<RoleResponse> adminRoles =
        Set.of(new RoleResponse(RoleName.USER.name()), new RoleResponse(RoleName.ADMIN.name()));
    List<UserResponse> expectedResponse =
        List.of(
            new UserResponse(
                1L, "Admin", "Admin", "admin", "admin@admin.com", true, true, adminRoles),
            new UserResponse(2L, "Test", "Test", "test", "test@test.com", true, true, userRoles));
    when(userRepository.findAllWithRoles()).thenReturn(List.of(admin, user));
    List<UserResponse> actualResponse = adminService.findAllUsers();
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void itShouldChangeUsersRoles() {
    ChangeRoleRequest changeRoleRequest =
        new ChangeRoleRequest(Collections.singleton("test"), Set.of(RoleName.USER, RoleName.ADMIN));
    User expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, true, true, adminRoles);
    when(userRepository.findAllByUsernameIn(changeRoleRequest.getUsernames()))
        .thenReturn(List.of(user));
    adminService.changeRole(changeRoleRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldLockUsers() {
    AdminRequest adminRequest = new AdminRequest(Collections.singleton("test"));
    User expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, true, false, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(List.of(user));
    adminService.lock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldUnlockUsers() {
    AdminRequest adminRequest = new AdminRequest(Collections.singleton("test2"));
    User expectedUser =
        new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, false, true, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(restricted));
    adminService.unlock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldActivateUsers() {
    AdminRequest adminRequest = new AdminRequest(Collections.singleton("test2"));
    User expectedUser =
        new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, true, false, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(restricted));
    adminService.activate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldDeactivateUsers() {
    AdminRequest adminRequest = new AdminRequest(Collections.singleton("test"));
    User expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, false, true, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(List.of(user));
    adminService.deactivate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldDeleteUsers() {
    AdminRequest adminRequest = new AdminRequest(Set.of("admin", "test", "test2"));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(admin, user, restricted));
    adminService.delete(adminRequest);
    verify(userRepository, times(3)).delete(any(User.class));
  }
}
