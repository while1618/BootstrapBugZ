package org.bootstrapbugz.api.admin.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.admin.service.impl.AdminServiceImpl;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.bootstrapbugz.api.user.response.UserResponse.RoleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
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

  private User user;
  private User admin;

  @BeforeEach
  void setUp(TestInfo info) {
    if (info.getTags().contains("skipBeforeEach")) return;

    Role userRole = new Role(RoleName.USER);
    Set<Role> adminRoles = Set.of(userRole, new Role(RoleName.ADMIN));
    admin =
        new User(1L, "Admin", "Admin", "admin", "admin@admin.com", null, true, true, adminRoles);
    user =
        new User(2L, "User", "User", "user", "user@user.com", null, true, true, Set.of(userRole));
  }

  @Test
  void itShouldFindAllUsersWithRoles() {
    RoleResponse userRole = new RoleResponse(RoleName.USER.name());
    Set<RoleResponse> adminRoles = Set.of(new RoleResponse(RoleName.ADMIN.name()), userRole);
    List<UserResponse> expectedResponse =
        List.of(
            new UserResponse(
                1L, "Admin", "Admin", "admin", "admin@admin.com", true, true, adminRoles),
            new UserResponse(
                2L, "User", "User", "user", "user@user.com", true, true, Set.of(userRole)));
    when(userRepository.findAllWithRoles()).thenReturn(List.of(admin, user));
    List<UserResponse> actualResponse = adminService.findAllUsers();
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void changeRole() {}

  @Test
  void lock() {}

  @Test
  void unlock() {}

  @Test
  void activate() {}

  @Test
  void deactivate() {}

  @Test
  void delete() {}
}
