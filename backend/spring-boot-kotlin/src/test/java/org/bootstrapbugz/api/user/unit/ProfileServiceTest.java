package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.service.VerificationTokenService;
import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.UnitTestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest;
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
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private AccessTokenService accessTokenService;
  @Mock private RefreshTokenService refreshTokenService;
  @Mock private VerificationTokenService verificationTokenService;
  @InjectMocks private ProfileServiceImpl profileService;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @BeforeEach
  void setUp() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal())
        .thenReturn(UserPrincipal.Companion.create(UnitTestUtil.getTestUser()));
  }

  @Test
  void findProfile() {
    final var expectedUserDTO =
        new UserDTO(2L, "Test", "Test", "test", "test@localhost", null, null, null, null);
    final var userDTO = profileService.find();
    assertThat(userDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void patchProfile_newUsernameAndEmail() {
    final var expectedUser =
        new User(
            2L,
            "User",
            "User",
            "user",
            "user@localhost",
            "password",
            false,
            false,
            LocalDateTime.now(),
            Set.of(new Role(RoleName.USER)));
    final var patchProfileRequest =
        new PatchProfileRequest("User", "User", "user", "user@localhost");
    var testUser = Optional.of(UnitTestUtil.getTestUser());
    when(userRepository.findById(2L)).thenReturn(testUser);
    when(userRepository.existsByUsername(patchProfileRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(patchProfileRequest.getEmail())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    profileService.patch(patchProfileRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(testUser.get());
  }

  @Test
  void patchProfile_sameUsernameAndEmail() {
    final var expectedUser =
        new User(
            2L,
            "User",
            "Test",
            "test",
            "test@localhost",
            "password",
            true,
            false,
            LocalDateTime.now(),
            Set.of(new Role(RoleName.USER)));
    final var patchProfileRequest = new PatchProfileRequest("User", null, null, null);
    var testUser = Optional.of(UnitTestUtil.getTestUser());
    when(userRepository.findById(2L)).thenReturn(testUser);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> expectedUser);
    profileService.patch(patchProfileRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(testUser.get());
  }

  @Test
  void patchProfile_throwConflict_usernameExists() {
    final var patchProfileRequest = new PatchProfileRequest("", "", "Admin", "");
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByUsername(patchProfileRequest.getUsername())).thenReturn(true);
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    assertThatThrownBy(() -> profileService.patch(patchProfileRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void patchProfile_throwConflict_emailExists() {
    final var patchProfileRequest = new PatchProfileRequest("", "", "", "admin@localhost");
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(userRepository.existsByEmail(patchProfileRequest.getEmail())).thenReturn(true);
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    assertThatThrownBy(() -> profileService.patch(patchProfileRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void changePassword() {
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    final var user = UnitTestUtil.getTestUser();
    user.setPassword(bCryptPasswordEncoder.encode("qwerty123"));
    when(userRepository.findById(2L)).thenReturn(Optional.of(user));
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
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123456", "qwerty1234", "qwerty1234");
    when(messageService.getMessage("currentPassword.wrong")).thenReturn("Wrong current password.");
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    assertThatThrownBy(() -> profileService.changePassword(changePasswordRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Wrong current password.");
  }

  @Test
  void delete() {
    profileService.delete();
    verify(userRepository, times(1)).deleteById(any(Long.class));
  }
}
