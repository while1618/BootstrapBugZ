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
import org.bootstrapbugz.api.auth.jwt.redis.repository.RefreshTokenStoreRepository;
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
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.UnitTestUtil;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private MessageService messageService;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenStoreRepository refreshTokenStoreRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @InjectMocks private AccessTokenServiceImpl accessTokenService;
  @InjectMocks private RefreshTokenServiceImpl refreshTokenService;
  @InjectMocks private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;
  @InjectMocks private ResetPasswordTokenServiceImpl resetPasswordTokenService;
  private AuthServiceImpl authService;

  @BeforeEach
  public void setUp() {
    authService =
        new AuthServiceImpl(
            userRepository,
            roleRepository,
            messageService,
            bCryptPasswordEncoder,
            accessTokenService,
            refreshTokenService,
            confirmRegistrationTokenService,
            resetPasswordTokenService);
    ReflectionTestUtils.setField(accessTokenService, "secret", "secret");
    ReflectionTestUtils.setField(accessTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(refreshTokenService, "secret", "secret");
    ReflectionTestUtils.setField(refreshTokenService, "tokenDuration", 604800);
    ReflectionTestUtils.setField(confirmRegistrationTokenService, "secret", "secret");
    ReflectionTestUtils.setField(confirmRegistrationTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(resetPasswordTokenService, "secret", "secret");
    ReflectionTestUtils.setField(resetPasswordTokenService, "tokenDuration", 900);
  }

  @Test
  void signUp() {
    final var expectedUserDTO =
        UserDTO.builder()
            .id(2L)
            .firstName("Test")
            .lastName("Test")
            .username("test")
            .email("test@localhost")
            .activated(false)
            .nonLocked(true)
            .roleDTOs(Set.of(new RoleDTO(RoleName.USER.name())))
            .build();
    final var signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@localhost", "qwerty123", "qwerty123");
    when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(new Role(RoleName.USER)));
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActivated(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    final var actualUserDTO = authService.signUp(signUpRequest);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void resendConfirmationEmail() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActivated(false);
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(testUser));
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void resendConfirmationEmail_throwResourceNotFound_userNotFound() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void resendConfirmationEmail_throwConflict_userAlreadyActivated() {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    when(userRepository.findByUsernameOrEmail("test", "test"))
        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void confirmRegistration() {
    final var expectedUser =
        User.builder()
            .id(2L)
            .firstName("Test")
            .lastName("Test")
            .username("test")
            .email("test@localhost")
            .activated(true)
            .roles(Set.of(new Role(RoleName.USER)))
            .build();
    final var token = confirmRegistrationTokenService.create(2L);
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActivated(false);
    when(userRepository.findById(2L)).thenReturn(Optional.of(testUser));
    authService.confirmRegistration(new ConfirmRegistrationRequest(token));
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void confirmRegistration_throwBadRequest_invalidToken() {
    final var token = confirmRegistrationTokenService.create(2L);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void confirmRegistration_throwConfilict_userAlreadyActivated() {
    final var token = confirmRegistrationTokenService.create(2L);
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(ConflictException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void refreshToken() {
    final var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    final var token = refreshTokenService.create(2L, Collections.emptySet(), "ip1");
    when(refreshTokenStoreRepository.existsById(token)).thenReturn(true);
    final var refreshTokenRequest = new RefreshTokenRequest(token);
    final var refreshTokenDTO = authService.refreshToken(refreshTokenRequest, request);
    assertThat(refreshTokenDTO.accessToken()).isNotNull();
    assertThat(refreshTokenDTO.refreshToken()).isNotNull();
  }

  @Test
  void signOut() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
    final var token = accessTokenService.create(2L, Collections.emptySet());
    final var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    request.addHeader(AuthUtil.AUTH_HEADER, token);
    assertDoesNotThrow(() -> authService.signOut(request));
  }

  @Test
  void signOutFromAllDevices() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
    assertDoesNotThrow(() -> authService.signOutFromAllDevices());
  }

  @Test
  void forgotPassword() {
    final var forgotPasswordRequest = new ForgotPasswordRequest("test@localhost");
    when(userRepository.findByEmail("test@localhost"))
        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    authService.forgotPassword(forgotPasswordRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void forgotPassword_throwResourceNotFound_userNotFound() {
    final var forgotPasswordRequest = new ForgotPasswordRequest("test@localhost");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void resetPassword() {
    final var token = resetPasswordTokenService.create(2L);
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    authService.resetPassword(resetPasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                resetPasswordRequest.password(), userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void resetPassword_throwForbidden_invalid() {
    final var token = resetPasswordTokenService.create(2L);
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void signedInUser() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
    final var expectedUserDTO =
        UserDTO.builder()
            .id(2L)
            .firstName("Test")
            .lastName("Test")
            .username("test")
            .email("test@localhost")
            .activated(true)
            .nonLocked(true)
            .roleDTOs(Set.of(new RoleDTO(RoleName.USER.name())))
            .build();
    final var actualUserDTO = authService.signedInUser();
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void checkUsernameAvailability() {
    when(userRepository.existsByUsername("test")).thenReturn(true);
    assertThat(authService.isUsernameAvailable("test")).isFalse();
  }

  @Test
  void checkEmailAvailability() {
    when(userRepository.existsByEmail("available@localhost")).thenReturn(false);
    assertThat(authService.isEmailAvailable("available@localhost")).isTrue();
  }
}
