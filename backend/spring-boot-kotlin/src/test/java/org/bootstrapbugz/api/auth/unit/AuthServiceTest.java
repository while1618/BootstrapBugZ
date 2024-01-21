package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
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
import org.bootstrapbugz.api.auth.jwt.service.impl.RefreshTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.ResetPasswordTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.VerificationTokenServiceImpl;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RegisterUserRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.VerificationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.VerifyEmailRequest;
import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ConflictException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.UnitTestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
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
import org.springframework.security.authentication.AuthenticationManager;
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
  @Mock private AuthenticationManager authenticationManager;
  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenStoreRepository refreshTokenStoreRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @InjectMocks private AccessTokenServiceImpl accessTokenService;
  @InjectMocks private RefreshTokenServiceImpl refreshTokenService;
  @InjectMocks private VerificationTokenServiceImpl verificationTokenService;
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
            authenticationManager,
            accessTokenService,
            refreshTokenService,
            verificationTokenService,
            resetPasswordTokenService);
    ReflectionTestUtils.setField(accessTokenService, "secret", "secret");
    ReflectionTestUtils.setField(accessTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(refreshTokenService, "secret", "secret");
    ReflectionTestUtils.setField(refreshTokenService, "tokenDuration", 604800);
    ReflectionTestUtils.setField(verificationTokenService, "secret", "secret");
    ReflectionTestUtils.setField(verificationTokenService, "tokenDuration", 900);
    ReflectionTestUtils.setField(resetPasswordTokenService, "secret", "secret");
    ReflectionTestUtils.setField(resetPasswordTokenService, "tokenDuration", 900);
  }

  @Test
  void registerUser() {
    final var expectedUserDTO =
        new UserDTO(2L, "Test", "Test", "test", "test@localhost", null, null, null, null);
    final var registerUserRequest =
        new RegisterUserRequest("Test", "Test", "test", "test@localhost", "qwerty123", "qwerty123");
    when(userRepository.existsByUsername(registerUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registerUserRequest.getEmail())).thenReturn(false);
    when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(new Role(RoleName.USER)));
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActive(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    final var actualUserDTO = authService.register(registerUserRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void registerUser_throwConflict_usernameExists() {
    final var registerUserRequest =
        new RegisterUserRequest("Test", "Test", "test", "test@localhost", "qwerty123", "qwerty123");
    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
    when(userRepository.existsByUsername(registerUserRequest.getUsername())).thenReturn(true);
    assertThatThrownBy(() -> authService.register(registerUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Username already exists.");
  }

  @Test
  void registerUser_throwConflict_emailExists() {
    final var registerUserRequest =
        new RegisterUserRequest("Test", "Test", "test", "test@localhost", "qwerty123", "qwerty123");
    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
    when(userRepository.existsByUsername(registerUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registerUserRequest.getEmail())).thenReturn(true);
    assertThatThrownBy(() -> authService.register(registerUserRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Email already exists.");
  }

  @Test
  void deleteTokens() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
    final var token = accessTokenService.create(2L, Collections.emptySet());
    assertDoesNotThrow(() -> authService.deleteTokens(token, "ip1"));
  }

  @Test
  void deleteTokensOnAllDevices() {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
    assertDoesNotThrow(() -> authService.deleteTokensOnAllDevices());
  }

  @Test
  void refreshToken() {
    final var token = refreshTokenService.create(2L, Collections.emptySet(), "ip1");
    when(refreshTokenStoreRepository.existsById(token)).thenReturn(true);
    final var authTokensDTO = authService.refreshTokens(token, "ip1");
    assertThat(authTokensDTO.getAccessToken()).isNotNull();
    assertThat(authTokensDTO.getRefreshToken()).isNotNull();
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
                resetPasswordRequest.getPassword(), userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void resetPassword_throwBadRequest_invalidToken() {
    final var token = resetPasswordTokenService.create(2L);
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void sendVerificationMail() {
    final var verificationEmailRequest = new VerificationEmailRequest("test");
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActive(false);
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(testUser));
    authService.sendVerificationMail(verificationEmailRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void sendVerificationMail_throwResourceNotFound_userNotFound() {
    final var verificationEmailRequest = new VerificationEmailRequest("test");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.sendVerificationMail(verificationEmailRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void verificationEmailRequest_throwConflict_userAlreadyActivated() {
    final var verificationEmailRequest = new VerificationEmailRequest("test");
    when(messageService.getMessage("user.active")).thenReturn("User already activated.");
    when(userRepository.findByUsernameOrEmail("test", "test"))
        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    assertThatThrownBy(() -> authService.sendVerificationMail(verificationEmailRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void verifyEmail() {
    final var expectedUser =
        User.builder()
            .id(2L)
            .firstName("Test")
            .lastName("Test")
            .username("test")
            .email("test@localhost")
            .active(true)
            .roles(Set.of(new Role(RoleName.USER)))
            .build();
    final var token = verificationTokenService.create(2L);
    final var testUser = UnitTestUtil.getTestUser();
    testUser.setActive(false);
    when(userRepository.findById(2L)).thenReturn(Optional.of(testUser));
    authService.verifyEmail(new VerifyEmailRequest(token));
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void verifyEmail_throwBadRequest_invalidToken() {
    final var token = verificationTokenService.create(2L);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    final var throwable =
        catchThrowable(() -> authService.verifyEmail(new VerifyEmailRequest(token)));
    assertThat(throwable).isInstanceOf(BadRequestException.class).hasMessage("Invalid token.");
  }

  @Test
  void verifyEmail_throwConflict_userAlreadyActivated() {
    final var token = verificationTokenService.create(2L);
    when(userRepository.findById(2L)).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
    when(messageService.getMessage("user.active")).thenReturn("User already activated.");
    final var throwable =
        catchThrowable(() -> authService.verifyEmail(new VerifyEmailRequest(token)));
    assertThat(throwable)
        .isInstanceOf(ConflictException.class)
        .hasMessage("User already activated.");
  }
}
