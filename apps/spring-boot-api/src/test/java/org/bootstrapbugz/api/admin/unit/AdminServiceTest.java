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
import org.bootstrapbugz.api.admin.payload.request.ChangeRoleRequest;
import org.bootstrapbugz.api.admin.service.impl.AdminServiceImpl;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.payload.response.RoleResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
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
  private final Set<Role> userRoles = Set.of(new Role(RoleName.USER));
  private final Set<Role> adminRoles = Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN));
  private final User admin =
      new User(1L, "Admin", "Admin", "admin", "admin@admin.com", null, true, true, adminRoles);
  private final User user =
      new User(2L, "Test", "Test", "test", "test@test.com", null, true, true, userRoles);
  private final User restricted =
      new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, false, false, userRoles);

  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @InjectMocks private AdminServiceImpl adminService;
  @Captor private ArgumentCaptor<Iterable<User>> userArgumentCaptor;

  @Test
  void itShouldFindAllUsersWithRoles() {
    var userRoleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var adminRoleResponses =
        Set.of(new RoleResponse(RoleName.USER.name()), new RoleResponse(RoleName.ADMIN.name()));
    var expectedUserResponses =
        List.of(
            new UserResponse(
                1L, "Admin", "Admin", "admin", "admin@admin.com", true, true, adminRoleResponses),
            new UserResponse(
                2L, "Test", "Test", "test", "test@test.com", true, true, userRoleResponses));
    when(userRepository.findAllWithRoles()).thenReturn(List.of(admin, user));
    var actualUserResponses = adminService.findAllUsers();
    assertThat(actualUserResponses).isEqualTo(expectedUserResponses);
  }

  @Test
  void itShouldChangeUsersRoles() {
    var changeRoleRequest =
        new ChangeRoleRequest(Collections.singleton("test"), Set.of(RoleName.USER, RoleName.ADMIN));
    var expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, true, true, adminRoles);
    when(userRepository.findAllByUsernameIn(changeRoleRequest.getUsernames()))
        .thenReturn(List.of(user));
    adminService.changeRole(changeRoleRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldLockUsers() {
    var adminRequest = new AdminRequest(Collections.singleton("test"));
    var expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, true, false, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(List.of(user));
    adminService.lock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldUnlockUsers() {
    var adminRequest = new AdminRequest(Collections.singleton("test2"));
    var expectedUser =
        new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, false, true, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(restricted));
    adminService.unlock(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldActivateUsers() {
    var adminRequest = new AdminRequest(Collections.singleton("test2"));
    var expectedUser =
        new User(3L, "Test 2", "Test 2", "test2", "test2@test.com", null, true, false, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(restricted));
    adminService.activate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldDeactivateUsers() {
    var adminRequest = new AdminRequest(Collections.singleton("test"));
    var expectedUser =
        new User(2L, "Test", "Test", "test", "test@test.com", null, false, true, userRoles);
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames())).thenReturn(List.of(user));
    adminService.deactivate(adminRequest);
    verify(userRepository, times(1)).saveAll(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(List.of(expectedUser));
  }

  @Test
  void itShouldDeleteUsers() {
    var adminRequest = new AdminRequest(Set.of("admin", "test", "test2"));
    when(userRepository.findAllByUsernameIn(adminRequest.getUsernames()))
        .thenReturn(List.of(admin, user, restricted));
    adminService.delete(adminRequest);
    verify(userRepository, times(3)).delete(any(User.class));
  }
}
