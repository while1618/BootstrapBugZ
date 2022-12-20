package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.ConfirmRegistrationTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.RoleService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleService roleService;
  @Mock private MessageService messageService;
  @Spy private UserMapperImpl userMapper;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private ConfirmRegistrationTokenService confirmRegistrationTokenService;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;

  @InjectMocks private ProfileServiceImpl profileService;

  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  private String password;
  private Set<Role> roles;

  @BeforeEach
  void setUp() {
    password = bCryptPasswordEncoder.encode("qwerty123");
    roles = Collections.singleton(new Role(Role.RoleName.USER));
    var user = new User(1L, "Test", "Test", "test", "test@test.com", password, true, true, roles);
    TestUtil.setAuth(auth, securityContext, user);
  }

  @Test
  void itShouldUpdateUser_newUsernameAndEmail() {
    var expectedUser =
        new User(1L, "User", "User", "user", "user@user.com", password, false, true, roles);
    var updateUserRequest = new UpdateProfileRequest("User", "User", "user", "user@user.com");
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
    profileService.update(updateUserRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void itShouldUpdateUser_sameUsernameAndEmail() {
    var expectedUser =
        new User(1L, "User", "User", "test", "test@test.com", password, true, true, roles);
    var updateUserRequest = new UpdateProfileRequest("User", "User", "test", "test@test.com");
    profileService.update(updateUserRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void updateUserShouldThrowBadRequest_usernameExists() {
    var updateUserRequest = new UpdateProfileRequest("User", "User", "user", "user@user.com");
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> profileService.update(updateUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void updateUserShouldThrowBadRequest_emailExists() {
    var updateUserRequest = new UpdateProfileRequest("User", "User", "user", "user@user.com");
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> profileService.update(updateUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void itShouldChangePassword() {
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    profileService.changePassword(changePasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                changePasswordRequest.getNewPassword(),
                userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void changePasswordShouldThrownBadRequest_wrongOldPassword() {
    when(messageService.getMessage("oldPassword.invalid")).thenReturn("Wrong old password.");
    var changePasswordRequest =
        new ChangePasswordRequest("qwerty123456", "qwerty1234", "qwerty1234");
    assertThatThrownBy(() -> profileService.changePassword(changePasswordRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Wrong old password.");
  }
}
