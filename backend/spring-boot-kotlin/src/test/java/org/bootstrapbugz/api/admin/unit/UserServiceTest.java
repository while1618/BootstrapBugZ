package org.bootstrapbugz.api.admin.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.admin.service.impl.UserServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.UnitTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private MessageService messageService;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @InjectMocks private UserServiceImpl userService;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @Test
  void createUser() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.existsByUsername(userRequest.username())).thenReturn(false);
    when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
    userService.create(userRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getUsername()).isEqualTo(userRequest.username());
  }

  @Test
  void createUser_throwConflict_usernameExists() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("test")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.existsByUsername(userRequest.username())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> userService.create(userRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void createUser_throwConflict_emailExists() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("test@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> userService.create(userRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void findAllUsers() {
    final var expectedUserDTOs =
        List.of(
            new UserDTO(
                1L,
                "Admin",
                "Admin",
                "admin",
                "admin@localhost",
                true,
                false,
                Set.of(new RoleDTO(RoleName.ADMIN.name()), new RoleDTO(RoleName.USER.name())),
                null),
            new UserDTO(
                2L,
                "Test",
                "Test",
                "test",
                "test@localhost",
                true,
                false,
                Set.of(new RoleDTO(RoleName.USER.name())),
                null));

    when(userRepository.findAll(PageRequest.of(0, 10)))
        .thenReturn(
            new PageImpl<>(List.of(UnitTestUtil.getAdminUser(), UnitTestUtil.getTestUser())));
    final var actualUserDTOs = userService.findAll(PageRequest.of(0, 10));
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void findUserById() {
    final var expectedUserDTO =
        new UserDTO(
            1L,
            "Admin",
            "Admin",
            "admin",
            "admin@localhost",
            true,
            false,
            Set.of(new RoleDTO(RoleName.ADMIN.name()), new RoleDTO(RoleName.USER.name())),
            null);

    when(userRepository.findWithRolesById(1L)).thenReturn(Optional.of(UnitTestUtil.getAdminUser()));
    final var actualUserDTO = userService.findById(1L);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserById_throwResourceNotFound() {
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findById(100L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void updateUser() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByUsername(userRequest.username())).thenReturn(false);
    when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
    userService.update(2L, userRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getUsername()).isEqualTo(userRequest.username());
  }

  @Test
  void updateUser_throwConflict_usernameExists() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("admin")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByUsername(userRequest.username())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> userService.update(2L, userRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void updateUser_throwConflict_emailExists() {
    final var userRequest =
        UserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("admin@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> userService.update(2L, userRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void patchUser() {
    final var patchUserRequest =
        PatchUserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByUsername(patchUserRequest.username())).thenReturn(false);
    when(userRepository.existsByEmail(patchUserRequest.email())).thenReturn(false);
    userService.patch(2L, patchUserRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getUsername()).isEqualTo(patchUserRequest.username());
  }

  @Test
  void patchUser_throwConflict_usernameExists() {
    final var patchUserRequest =
        PatchUserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("admin")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByUsername(patchUserRequest.username())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> userService.patch(2L, patchUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void patchUser_throwConflict_emailExists() {
    final var patchUserRequest =
        PatchUserRequest.builder()
            .firstName("User")
            .lastName("User")
            .username("user")
            .email("admin@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .active(true)
            .lock(false)
            .roleNames(Set.of(RoleName.USER))
            .build();
    when(userRepository.findWithRolesById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByEmail(patchUserRequest.email())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> userService.patch(2L, patchUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void deleteUser() {
    userService.delete(1L);
    verify(userRepository, times(1)).deleteById(1L);
  }
}
