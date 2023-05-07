package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.ConfirmRegistrationTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private MessageService messageService;
  @Spy private UserMapperImpl userMapper;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private ConfirmRegistrationTokenService confirmRegistrationTokenService;
  @InjectMocks private ProfileServiceImpl profileService;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @BeforeEach
  void setUp() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(TestUtil.getTestUser()));
  }

  @Test
  void updateUser_newUsernameAndEmail() {
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("User")
            .setLastName("User")
            .setUsername("user")
            .setEmail("user@localhost")
            .setActivated(false)
            .setRoles(Set.of(new Role(RoleName.USER)));
    final var updateUserRequest =
        new UpdateProfileRequest("User", "User", "user", "user@localhost");
    when(userRepository.findByUsernameWithRoles("test"))
        .thenReturn(Optional.of(TestUtil.getTestUser()));
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
    profileService.update(updateUserRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void updateUser_sameUsernameAndEmail() {
    final var expectedUser =
        new User()
            .setId(2L)
            .setFirstName("User")
            .setLastName("User")
            .setUsername("test")
            .setEmail("test@localhost")
            .setActivated(true)
            .setRoles(Set.of(new Role(RoleName.USER)));
    final var updateUserRequest =
        new UpdateProfileRequest("User", "User", "test", "test@localhost");
    when(userRepository.findByUsernameWithRoles("test"))
        .thenReturn(Optional.of(TestUtil.getTestUser()));
    profileService.update(updateUserRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void updateUser_throwBadRequest_usernameExists() {
    final var updateUserRequest =
        new UpdateProfileRequest("Test", "Test", "admin", "test@localhost");
    when(userRepository.findByUsernameWithRoles("test"))
        .thenReturn(Optional.of(TestUtil.getTestUser()));
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> profileService.update(updateUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void updateUser_throwBadRequest_emailExists() {
    final var updateUserRequest =
        new UpdateProfileRequest("Test", "Test", "test", "admin@localhost");
    when(userRepository.findByUsernameWithRoles("test"))
        .thenReturn(Optional.of(TestUtil.getTestUser()));
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> profileService.update(updateUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void changePassword() {
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    final var testUser = TestUtil.getTestUser();
    testUser.setPassword(bCryptPasswordEncoder.encode("qwerty123"));
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
    profileService.changePassword(changePasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                changePasswordRequest.getNewPassword(),
                userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void changePassword_thrownBadRequest_wrongOldPassword() {
    when(messageService.getMessage("oldPassword.invalid")).thenReturn("Wrong old password.");
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123456", "qwerty1234", "qwerty1234");
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(TestUtil.getTestUser()));
    assertThatThrownBy(() -> profileService.changePassword(changePasswordRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Wrong old password.");
  }
}
