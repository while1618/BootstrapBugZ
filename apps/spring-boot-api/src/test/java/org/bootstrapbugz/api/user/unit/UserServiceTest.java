package org.bootstrapbugz.api.user.unit;

import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.response.RoleResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  private final Set<Role> userRoles = Set.of(new Role(RoleName.USER));
  private final Set<Role> adminRoles = Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN));
  private final User user =
      new User(1L, "Test", "Test", "test", "test@test.com", null, true, true, userRoles);
  private final User admin =
      new User(2L, "Admin", "Admin", "admin", "admin@admin.com", null, true, true, adminRoles);
  @Mock private UserRepository userRepository;
  @Mock private MessageService messageService;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @InjectMocks private UserServiceImpl userService;

  @Test
  void itShouldFindAllUsersWithoutRolesAndEmails() {
    var expectedUserResponses =
        List.of(
            new UserResponse(1L, "Test", "Test", "test", null, true, true, null),
            new UserResponse(2L, "Admin", "Admin", "admin", null, true, true, null));
    when(userRepository.findAll()).thenReturn(List.of(user, admin));
    var actualUserResponses = userService.findAll();
    assertThat(actualUserResponses).isEqualTo(expectedUserResponses);
  }

  @Test
  void itShouldFindAllUsersWithRolesAndEmails() {
    TestUtil.setAuth(auth, securityContext, admin);
    var userRoleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var adminRoleResponses =
        Set.of(new RoleResponse(RoleName.USER.name()), new RoleResponse(RoleName.ADMIN.name()));
    var expectedUserResponses =
        List.of(
            new UserResponse(
                1L, "Test", "Test", "test", "test@test.com", true, true, userRoleResponses),
            new UserResponse(
                2L, "Admin", "Admin", "admin", "admin@admin.com", true, true, adminRoleResponses));
    when(userRepository.findAllWithRoles()).thenReturn(List.of(user, admin));
    var actualUserResponses = userService.findAll();
    assertThat(actualUserResponses).isEqualTo(expectedUserResponses);
  }

  @Test
  void itShouldFindUserByUsername_showEmail() {
    TestUtil.setAuth(auth, securityContext, user);
    var expectedUserResponse =
        new UserResponse(1L, "Test", "Test", "test", "test@test.com", true, true, null);
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    var actualUserResponse = userService.findByUsername("test");
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() {
    var expectedUserResponse =
        new UserResponse(2L, "Admin", "Admin", "admin", null, true, true, null);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
    var actualUserResponse = userService.findByUsername("admin");
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void itShouldFindUserByUsername_adminSignedIn() {
    TestUtil.setAuth(auth, securityContext, admin);
    var userRoleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(
            1L, "Test", "Test", "test", "test@test.com", true, true, userRoleResponses);
    when(userRepository.findByUsernameWithRoles("test")).thenReturn(Optional.of(user));
    var actualUserResponse = userService.findByUsername("test");
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() {
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findByUsername("test"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }
}
