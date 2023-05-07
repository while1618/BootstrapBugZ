package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.RoleRepository;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  private final Set<Role> userRoles = Set.of(new Role(RoleName.USER));
  private final Set<Role> adminRoles = Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN));
  private final User user =
      new User(
          1L,
          "Test",
          "Test",
          "test",
          "test@test.com",
          null,
          true,
          true,
          LocalDateTime.now(),
          userRoles);
  private final User admin =
      new User(
          2L,
          "Admin",
          "Admin",
          "admin",
          "admin@admin.com",
          null,
          true,
          true,
          LocalDateTime.now(),
          adminRoles);
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private MessageService messageService;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @InjectMocks private UserServiceImpl userService;

  @Test
  void itShouldFindAllUsersWithoutRolesAndEmails() {
    final var expectedUserDTOs =
        List.of(
            new UserDTO(1L, "Test", "Test", "test", null, true, true, LocalDateTime.now(), null),
            new UserDTO(
                2L, "Admin", "Admin", "admin", null, true, true, LocalDateTime.now(), null));
    when(userRepository.findAll()).thenReturn(List.of(user, admin));
    final var actualUserDTOs = userService.findAll();
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void itShouldFindAllUsersWithRolesAndEmails() {
    TestUtil.setAuth(auth, securityContext, admin);
    final var userRoleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var adminRoleDTOs =
        Set.of(new RoleDTO(RoleName.USER.name()), new RoleDTO(RoleName.ADMIN.name()));
    final var expectedUserDTOs =
        List.of(
            new UserDTO(
                1L,
                "Test",
                "Test",
                "test",
                "test@test.com",
                true,
                true,
                LocalDateTime.now(),
                userRoleDTOs),
            new UserDTO(
                2L,
                "Admin",
                "Admin",
                "admin",
                "admin@admin.com",
                true,
                true,
                LocalDateTime.now(),
                adminRoleDTOs));
    when(userRepository.findAllWithRoles()).thenReturn(List.of(user, admin));
    final var actualUserDTOs = userService.findAll();
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void itShouldFindUserByUsername_showEmail() {
    TestUtil.setAuth(auth, securityContext, user);
    final var expectedUserDTO =
        new UserDTO(
            1L, "Test", "Test", "test", "test@test.com", true, true, LocalDateTime.now(), null);
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    final var actualUserDTO = userService.findByUsername("test");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() {
    final var expectedUserDTO =
        new UserDTO(2L, "Admin", "Admin", "admin", null, true, true, LocalDateTime.now(), null);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
    final var actualUserDTO = userService.findByUsername("admin");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void itShouldFindUserByUsername_adminSignedIn() {
    TestUtil.setAuth(auth, securityContext, admin);
    final var userRoleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var expectedUserDTO =
        new UserDTO(
            1L,
            "Test",
            "Test",
            "test",
            "test@test.com",
            true,
            true,
            LocalDateTime.now(),
            userRoleDTOs);
    when(userRepository.findByUsernameWithRoles("test")).thenReturn(Optional.of(user));
    final var actualUserDTO = userService.findByUsername("test");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() {
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findByUsername("test"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }
}
