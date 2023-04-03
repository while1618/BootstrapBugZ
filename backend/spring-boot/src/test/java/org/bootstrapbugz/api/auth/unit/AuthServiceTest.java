package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.jwt.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.jwt.service.impl.AccessTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.ConfirmRegistrationTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.RefreshTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.ResetPasswordTokenServiceImpl;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private MessageService messageService;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;

  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  private AccessTokenServiceImpl accessTokenService;
  private RefreshTokenServiceImpl refreshTokenService;
  private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;
  private ResetPasswordTokenServiceImpl resetPasswordTokenService;

  private AuthServiceImpl authService;

  private String password;
  private Role role;
  private Set<Role> roles;
  private User user;

  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @BeforeEach
  public void setUp() {
    accessTokenService =
        new AccessTokenServiceImpl(
            accessTokenBlacklistRepository, userBlacklistRepository, messageService);
    refreshTokenService =
        new RefreshTokenServiceImpl(refreshTokenWhitelistRepository, messageService);
    confirmRegistrationTokenService = new ConfirmRegistrationTokenServiceImpl(eventPublisher);
    resetPasswordTokenService =
        new ResetPasswordTokenServiceImpl(userBlacklistRepository, messageService, eventPublisher);
    ReflectionTestUtils.setField(accessTokenService, "secret", "secret");
    ReflectionTestUtils.setField(accessTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(refreshTokenService, "secret", "secret");
    ReflectionTestUtils.setField(refreshTokenService, "tokenDuration", 604800);
    ReflectionTestUtils.setField(confirmRegistrationTokenService, "secret", "secret");
    ReflectionTestUtils.setField(confirmRegistrationTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(resetPasswordTokenService, "secret", "secret");
    ReflectionTestUtils.setField(resetPasswordTokenService, "tokenDuration", 900);
    authService =
        new AuthServiceImpl(
            userRepository,
            roleRepository,
            messageService,
            bCryptPasswordEncoder,
            userMapper,
            accessTokenService,
            refreshTokenService,
            confirmRegistrationTokenService,
            resetPasswordTokenService);
    password = bCryptPasswordEncoder.encode("qwerty123");
    role = new Role(RoleName.USER);
    roles = Set.of(role);
    user = new User(1L, "Test", "Test", "test", "test@test.com", password, false, true, roles);
  }

  @Test
  void itShouldSignUp() {
    final var roleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var expectedUserDTO =
        new UserDTO(1L, "Test", "Test", "test", "test@test.com", false, true, roleDTOs);
    final var signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@test.com", "qwerty123", "qwerty123");
    when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));
    when(userRepository.save(any(User.class))).thenReturn(user);
    final var actualUserDTO = authService.signUp(signUpRequest);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void itShouldResendConfirmationEmail() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldConfirmRegistration() {
    final var expectedUser =
        new User(1L, "Test", "Test", "test", "test@test.com", password, true, true, roles);
    final var token = confirmRegistrationTokenService.create(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    authService.confirmRegistration(new ConfirmRegistrationRequest(token));
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalid() {
    final var token = confirmRegistrationTokenService.create(1L);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() {
    final var token = confirmRegistrationTokenService.create(1L);
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldRefreshToken() {
    final var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    final var token = refreshTokenService.create(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(token)).thenReturn(true);
    final var refreshTokenRequest = new RefreshTokenRequest(token);
    final var refreshTokenDTO = authService.refreshToken(refreshTokenRequest, request);
    assertThat(refreshTokenDTO.getAccessToken()).isNotNull();
    assertThat(refreshTokenDTO.getRefreshToken()).isNotNull();
  }

  @Test
  void itShouldSignOut() {
    TestUtil.setAuth(auth, securityContext, user);
    final var token = accessTokenService.create(1L, Collections.emptySet());
    final var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    request.addHeader(AuthUtil.AUTH_HEADER, token);
    assertDoesNotThrow(() -> authService.signOut(request));
  }

  @Test
  void itShouldSignOutFromAllDevices() {
    TestUtil.setAuth(auth, securityContext, user);
    assertDoesNotThrow(() -> authService.signOutFromAllDevices());
  }

  @Test
  void itShouldForgotPassword() {
    final var forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    authService.forgotPassword(forgotPasswordRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() {
    final var forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void itShouldResetPassword() {
    final var token = resetPasswordTokenService.create(1L);
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    authService.resetPassword(resetPasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                resetPasswordRequest.getPassword(), userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalid() {
    final var token = resetPasswordTokenService.create(1L);
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldReceiveSignedInUser() {
    TestUtil.setAuth(auth, securityContext, user);
    final var roleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var expectedUserDTO =
        new UserDTO(1L, "Test", "Test", "test", "test@test.com", false, true, roleDTOs);
    final var actualUserDTO = authService.signedInUser();
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void itShouldCheckUsernameAvailability() {
    when(userRepository.existsByUsername("user")).thenReturn(true);
    assertThat(authService.isUsernameAvailable("user")).isFalse();
  }

  @Test
  void itShouldCheckEmailAvailability() {
    when(userRepository.existsByEmail("available@bootstrapbugz.com")).thenReturn(false);
    assertThat(authService.isEmailAvailable("available@bootstrapbugz.com")).isTrue();
  }
}
