package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private MessageService messageService;
  @Spy private UserMapperImpl userMapper;
  @InjectMocks private UserServiceImpl userService;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;

  @BeforeEach
  void setUp() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void findAllUsers_rolesAndEmailsHidden() {
    when(auth.getPrincipal()).thenReturn("anonymousUser");
    final var expectedUserDTOs =
        List.of(
            new UserDTO()
                .setId(1L)
                .setFirstName("Admin")
                .setLastName("Admin")
                .setUsername("admin")
                .setActivated(true)
                .setNonLocked(true),
            new UserDTO()
                .setId(2L)
                .setFirstName("Test")
                .setLastName("Test")
                .setUsername("test")
                .setActivated(true)
                .setNonLocked(true));
    when(userRepository.findAll())
        .thenReturn(List.of(TestUtil.getAdminUser(), TestUtil.getTestUser()));
    final var actualUserDTOs = userService.findAll();
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void findAllUsers_rolesAndEmailsShown() {
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(TestUtil.getAdminUser()));
    final var expectedUserDTOs =
        List.of(
            new UserDTO()
                .setId(1L)
                .setFirstName("Admin")
                .setLastName("Admin")
                .setUsername("admin")
                .setEmail("admin@localhost")
                .setActivated(true)
                .setNonLocked(true)
                .setRoleDTOs(
                    Set.of(new RoleDTO(RoleName.USER.name()), new RoleDTO(RoleName.ADMIN.name()))),
            new UserDTO()
                .setId(2L)
                .setFirstName("Test")
                .setLastName("Test")
                .setUsername("test")
                .setEmail("test@localhost")
                .setActivated(true)
                .setNonLocked(true)
                .setRoleDTOs(Set.of(new RoleDTO(RoleName.USER.name()))));
    when(userRepository.findAllWithRoles())
        .thenReturn(List.of(TestUtil.getAdminUser(), TestUtil.getTestUser()));
    final var actualUserDTOs = userService.findAll();
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void findUserByUsername_emailHidden() {
    when(auth.getPrincipal()).thenReturn("anonymousUser");
    final var expectedUserDTO =
        new UserDTO()
            .setId(1L)
            .setFirstName("Admin")
            .setLastName("Admin")
            .setUsername("admin")
            .setNonLocked(true)
            .setActivated(true);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(TestUtil.getAdminUser()));
    final var actualUserDTO = userService.findByUsername("admin");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsername_emailShown() {
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(TestUtil.getTestUser()));
    final var expectedUserDTO =
        new UserDTO()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setNonLocked(true)
            .setActivated(true);
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(TestUtil.getTestUser()));
    final var actualUserDTO = userService.findByUsername("test");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsername_adminSignedIn() {
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(TestUtil.getAdminUser()));
    final var expectedUserDTO =
        new UserDTO()
            .setId(2L)
            .setFirstName("Test")
            .setLastName("Test")
            .setUsername("test")
            .setEmail("test@localhost")
            .setNonLocked(true)
            .setActivated(true)
            .setRoleDTOs(Set.of(new RoleDTO(RoleName.USER.name())));
    when(userRepository.findByUsernameWithRoles("test"))
        .thenReturn(Optional.of(TestUtil.getTestUser()));
    final var actualUserDTO = userService.findByUsername("test");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsername_throwResourceNotFound() {
    when(auth.getPrincipal()).thenReturn("anonymousUser");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findByUsername("test"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }
}
